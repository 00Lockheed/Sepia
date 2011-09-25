package edu.cwru.SimpleRTS.agent;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableCollection;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.environment.State;
import edu.cwru.SimpleRTS.model.SimpleModel;
import edu.cwru.SimpleRTS.model.SimplePlanner;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.DistanceMetrics;
import edu.cwru.SimpleRTS.util.TypeLoader;


public class ScriptedGoalAgentTest {
	static SimpleModel model;
	static SimplePlanner planner;
	static List<Template> templates;
	static State state;
	static int player=0;
	static Unit founder;
	@BeforeClass
	public static void loadTemplates() throws Exception {
		templates = TypeLoader.loadFromFile("data/unit_templates",player);		
		System.out.println("Sucessfully loaded templates");
		State.StateBuilder builder = new State.StateBuilder();
		builder.setSize(15,15);
		for (Template t : templates) {
			builder.addTemplate(t, player);
		}
		
		
		{
			
			Unit u = ((UnitTemplate)builder.getTemplate(player, "Peasant")).produceInstance();
			u.setxPosition(5);
			u.setyPosition(5);
			founder = u;
			builder.addUnit(u);
		}
		{
			ResourceNode rn = new ResourceNode(ResourceNode.Type.GOLD_MINE, 2, 2, 70000);
			builder.addResource(rn);
		}
		state = builder.build();
		planner = new SimplePlanner(state);
		model=new SimpleModel(state, 1235);
	}
	
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() {
		//Get the resources right
		state.depositResources(player, ResourceType.GOLD, 1200);
		state.depositResources(player, ResourceType.WOOD, 800);
		String commands="Build:TownHall:0:0\n";/*+
				"Transfer:1:Idle:Gold\n" +
				"Produce:Peasant\n" +
				"Transfer:1:Idle:Gold\n" +
				"Produce:Peasant\n" +
				"Transfer:1:Idle:Gold\n" +
				"Produce:Peasant\n" +
				"Transfer:1:Idle:Gold\n" +
				"Wait:Gold:200\n" +
				"Build:Barracks:2:-2\n" +
				"Attack:All\n";*/
		int ncommands = 11;
		BufferedReader commandreader = new BufferedReader(new StringReader(commands));
		ScriptedGoalAgent agent = new ScriptedGoalAgent(0,commandreader,true);
		
		for (int step = 0; step<30; step++)
		{
			CountDownLatch latch = new CountDownLatch(1);
			if (step == 0)
			{
				agent.acceptInitialState(model.getState(), latch);
			}
			else
			{
				agent.acceptMiddleState(model.getState(), latch);
			}
			try
			{
				latch.await();
			}
			catch(InterruptedException e)
			{
				//TODO: handle this somehow
				e.printStackTrace();
				System.exit(-1);
			}
			ImmutableCollection<Action> actionsimmut = agent.getAction().values();
			Action[] actions = new Action[actionsimmut.size()];
			{
				int i = 0;
				for (Action a : actionsimmut)
				{
					actions[i] = a;
					i++;
				}
			}
			System.out.println("Actions:");
			for (Action a : actions) {
				System.out.println(a);
			}
			System.out.println("Assets("+state.getUnits(player).values().size()+"):");
			Collection<Unit> units = state.getUnits(player).values();
			for (Unit u : units) {
				System.out.println(u.getTemplate().getName() + " (ID: "+u.ID+") at "+u.getxPosition() + "," + u.getyPosition());
			}
			System.out.println("All agents control a combined " + state.getUnits().values().size() + " units");
			System.out.println(state.getResourceAmount(player, ResourceType.GOLD)+" Gold");
			System.out.println(state.getResourceAmount(player, ResourceType.WOOD)+" Wood");
			System.out.println(state.getSupplyAmount(player)+"/"+state.getSupplyCap(player) + " Food");
			model.setActions(actions);
			model.executeStep();
		}
	}
}