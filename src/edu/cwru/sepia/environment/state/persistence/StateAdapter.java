package edu.cwru.sepia.environment.state.persistence;

import java.util.List;

import edu.cwru.sepia.environment.PlayerState;
import edu.cwru.sepia.environment.State;
import edu.cwru.sepia.environment.State.StateBuilder;
import edu.cwru.sepia.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.sepia.environment.state.persistence.generated.XmlResourceNode;
import edu.cwru.sepia.environment.state.persistence.generated.XmlState;
import edu.cwru.sepia.model.resource.ResourceNode;

public class StateAdapter {
	
	PlayerAdapter playerAdapter = new PlayerAdapter();
	ResourceNodeAdapter resourceNodeAdapter = new ResourceNodeAdapter();

	public XmlState toXml(State state) {
		XmlState xml = new XmlState();
		
		List<XmlPlayer> players = xml.getPlayer();
		for(PlayerState ps : state.getPlayerStates())
		{
			players.add(playerAdapter.toXml(ps));
		}
		
		List<XmlResourceNode> resources = xml.getResourceNode();
		for(ResourceNode rn : state.getResources())
		{
			resources.add(resourceNodeAdapter.toXml(rn));
		}
		
		xml.setXExtent(state.getXExtent());		
		xml.setYExtent(state.getYExtent());
		xml.setNextTemplateID(state.getNextTemplateIDForXMLSave());
		xml.setNextTargetID(state.getNextTargetIDForXMLSave());
		xml.setFogOfWar(state.getFogOfWar());
//		xml.setRevealedResources(state.getRevealedResources());
		return xml;
	}
	
	public State fromXml(XmlState xml) {
		StateBuilder builder = new StateBuilder();
		
		for(XmlPlayer player : xml.getPlayer())
		{
			builder.addPlayer(playerAdapter.fromXml(player));
		}
		
		
		if(xml.getResourceNode() != null)
		{
			for(XmlResourceNode resource : xml.getResourceNode())
			{
				builder.addResource(resourceNodeAdapter.fromXml(resource));
			}
		}
		
		builder.setSize(xml.getXExtent(), xml.getYExtent());
		builder.setIDDistributerTargetMax(xml.getNextTargetID());
		builder.setIDDistributerTemplateMax(xml.getNextTemplateID());
		State state = builder.build();
		state.updateGlobalListsFromPlayers();
		state.recalculateVision();
		state.setFogOfWar(xml.isFogOfWar());
//		state.setRevealedResources(xml.isRevealedResources());
		return state;
	}
}