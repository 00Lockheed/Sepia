package edu.cwru.SimpleRTS;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.environment.StateCreator;
import edu.cwru.SimpleRTS.environment.XmlStateCreator;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlState;
import edu.cwru.SimpleRTS.util.config.xml.Configuration;
import edu.cwru.SimpleRTS.util.config.xml.ModelParameters;
import edu.cwru.SimpleRTS.util.config.xml.Player;
import edu.cwru.SimpleRTS.util.config.xml.Player.AgentClass;
import edu.cwru.SimpleRTS.util.config.xml.Runner;

public class Main2 {

	public static void main(String[] args) {
		if(args.length == 0)
		{
			System.out.println("You must specify a configuration file.");
			return;
		}
				
		JAXBContext context;
		Configuration xmlConfig = null;
		try 
		{
			context = JAXBContext.newInstance(XmlState.class);
			xmlConfig = (Configuration)context.createUnmarshaller().unmarshal(new File(args[0]));
		} 
		catch (JAXBException e1) 
		{
			System.out.println(args[0] + " is not a valid XML configuration file.");
			return;
		}
		
		StateCreator stateCreator;
		try
		{
			stateCreator = getStateCreator(xmlConfig);
		}
		catch(JAXBException ex) 
		{
			System.out.println(xmlConfig.getMap() + " is not a valid map file.");
			return;
		}
		
		Agent[] agents = getAgents(xmlConfig);
		if(agents == null)
			return;
		
		edu.cwru.SimpleRTS.environment.Runner runner;
		try
		{
			runner = getRunner(xmlConfig, stateCreator, agents);
		}
		catch (Exception e)
		{
			System.out.println("Unable to instantiate episode runner " + xmlConfig.getRunner().getRunnerClass());
			e.printStackTrace();
			return;
		}
		
		runner.run();
	}
	
	private static StateCreator getStateCreator(Configuration xmlConfig) throws JAXBException {
		String mapFilename = xmlConfig.getMap();
		JAXBContext context = JAXBContext.newInstance(XmlState.class);
		XmlState state = (XmlState)context.createUnmarshaller().unmarshal(new File(mapFilename));
		return new XmlStateCreator(state);
	}
	
	private static void getModelParameters(Configuration xmlConfig) {
		ModelParameters modelParameters = xmlConfig.getModelParameters();
		//TODO
	}
	
	private static Agent[] getAgents(Configuration xmlConfig) {
		List<Player> players = xmlConfig.getPlayer();
		Agent[] agents = new Agent[players.size()];
		
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.get(i);
			AgentClass agentClass = player.getAgentClass();
			try
			{
				Class<?> classDef = Class.forName(agentClass.getClassName());
				//TODO - handle invalid player Ids
				try 
				{
					
					Agent agent = (Agent) classDef.getConstructor(int.class, String[].class).newInstance(player.getId(), agentClass.getArgument().toArray(new String[0]));
					agents[i] = agent;
				} 
				catch (Exception e) 
				{
					Agent agent = (Agent) classDef.getConstructor(int.class).newInstance(player.getId());
					agents[i] = agent;
				}
			}
			catch(Exception ex)
			{
				System.out.println("Unable to instantiate " + agentClass.getClassName() + " with arguments " + agentClass.getArgument() + ".");
				return null;
			}
		}
		
		return agents;
	}
	
	private static edu.cwru.SimpleRTS.environment.Runner getRunner(Configuration xmlConfig, StateCreator stateCreator, Agent[] agents) throws ClassNotFoundException, IllegalArgumentException, 
													SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Runner runner = xmlConfig.getRunner();
		Class<?> runnerClass = Class.forName(runner.getRunnerClass());
		edu.cwru.SimpleRTS.util.Configuration config = new edu.cwru.SimpleRTS.util.Configuration();
		//TODO - fill configuration from runner parameters
		return (edu.cwru.SimpleRTS.environment.Runner)
				runnerClass
				.getConstructor(edu.cwru.SimpleRTS.util.Configuration.class, StateCreator.class, Agent[].class)
				.newInstance(config, stateCreator, agents);
	}
}
