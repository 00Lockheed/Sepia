package edu.cwru.SimpleRTS.agent;


import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.cwru.SimpleRTS.action.Action;
import edu.cwru.SimpleRTS.action.ActionType;
import edu.cwru.SimpleRTS.action.TargetedAction;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.Type;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

/**
 * This agent will first collect gold to produce a peasant,
 * then the two peasants will collect gold and wood separately until reach goal.
 * @author Feng
 *
 */
public class MyAgent extends Agent {

	private static final long serialVersionUID = -4047208702628325380L;

	private int goldRequired;
	private int woodRequired;
	
	public MyAgent(int playernum) {
		super(playernum);
		Preferences prefs = Preferences.userRoot().node("edu").node("cwru").node("SimpleRTS").node("model");
		goldRequired = prefs.getInt("RequiredGold", 0);
		woodRequired = prefs.getInt("RequiredWood", 0);
	}

	StateView currentState;
	
	@Override
	public Builder<Integer, Action> initialStep(StateView newstate) {
		return middleStep(newstate);
	}

	@Override
	public ImmutableMap.Builder<Integer,Action> middleStep(StateView newState) {
		ImmutableMap.Builder<Integer,Action> builder = new ImmutableMap.Builder<Integer,Action>();
		currentState = newState;
		System.out.println("All units: " + currentState.getAllUnitIds());
		
		int currentGold = currentState.getResourceAmount(0, ResourceType.GOLD);
		int currentWood = currentState.getResourceAmount(0, ResourceType.WOOD);
		System.out.println("Current Gold: " + currentGold);
		System.out.println("Current Wood: " + currentWood);
		List<Integer> allUnitIds = currentState.getAllUnitIds();
		List<Integer> peasantIds = new ArrayList<Integer>();
		List<Integer> townhallIds = new ArrayList<Integer>();
		for(int i=0; i<allUnitIds.size(); i++) {
			int id = allUnitIds.get(i);
			UnitView unit = currentState.getUnit(id);
			String unitTypeName = unit.getTemplateView().getUnitName();
			if(unitTypeName.equals("TownHall"))
				townhallIds.add(id);
			if(unitTypeName.equals("Peasant"))
				peasantIds.add(id);
		}
		
		if(peasantIds.size()>=2) {  // collect resources
			if(currentWood<woodRequired) {
				int peasantId = peasantIds.get(1);
				Unit.UnitView peasant = currentState.getUnit(peasantId);
				int townhallId = 0;
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.TREE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
			else if(currentGold<goldRequired) {
				int peasantId = peasantIds.get(0);
				Unit.UnitView peasant = currentState.getUnit(peasantId);
				int townhallId = 0;
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoType() == ResourceType.GOLD && currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.GOLD_MINE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
		}
		else {  // build peasant
			if(currentGold>=400) {
				System.err.println("already have enough gold to produce a new peasant.");
				TemplateView peasanttemplate = currentState.getTemplate(playernum, "Peasant");
				int peasanttemplateID = peasanttemplate.getID();
				int townhallID = 0;
				builder.put(townhallID, Action.createCompoundProduction(townhallID, peasanttemplateID));
			} else {
				int peasantId = peasantIds.get(0);
				Unit.UnitView peasant = currentState.getUnit(peasantId);
				int townhallId = 0;
				Action b = null;
				if(currentState.getUnit(peasantId).getCargoType() == ResourceType.GOLD && currentState.getUnit(peasantId).getCargoAmount()>0)
					b = new TargetedAction(peasantId, ActionType.COMPOUNDDEPOSIT, townhallId);
				else {
					List<Integer> resourceIds = currentState.getResourceNodeIds(Type.GOLD_MINE);
					b = new TargetedAction(peasantId, ActionType.COMPOUNDGATHER, resourceIds.get(0));
				}
				builder.put(peasantId, b);
			}
		}
		return builder;
	}

	@Override
	public void terminalStep(StateView newstate) {
		int currentGold = currentState.getResourceAmount(0, ResourceType.GOLD);
		int currentWood = currentState.getResourceAmount(0, ResourceType.WOOD);
		System.out.println("Current Gold: " + currentGold);
		System.out.println("Current Wood: " + currentWood);
		System.out.println("Congratulations! You finish the task!");
	}
	
	public static String getUsage() {
		return "None";
	}

}
