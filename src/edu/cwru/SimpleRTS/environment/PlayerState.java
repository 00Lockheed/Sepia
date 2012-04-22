package edu.cwru.SimpleRTS.environment;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.DeepEquatable;

public class PlayerState implements Serializable, DeepEquatable {
	private static final long serialVersionUID = 1L;

	public final int playerNum;
	private HashMap<Integer,Unit> units;
	@SuppressWarnings("rawtypes")
	private Map<Integer,Template> templates;
	private Set<Integer> upgrades;
	private Map<ResourceType,Integer> currentResources;
	private int currentSupply;
	private int currentSupplyCap;
	private StateView view;
	private int[][] canSee;
	
	@SuppressWarnings("rawtypes")
	public PlayerState(int id) {
		this.playerNum = id;
		units = new HashMap<Integer,Unit>();
		templates = new HashMap<Integer,Template>();
		upgrades = new HashSet<Integer>();
		currentResources = new EnumMap<ResourceType,Integer>(ResourceType.class);	
	}
	
	public Unit getUnit(int id) {
		return units.get(id);
	}
	
	public Map<Integer,Unit> getUnits() {
		return units;
	}
	
	public void addUnit(Unit unit) {
		units.put(unit.ID, unit);
	}
	
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int id) {
		return templates.get(id);
	}

	@SuppressWarnings("rawtypes")
	public Template getTemplate(String name) {
		for(Template t : templates.values())
		{
			if(name.equals(t.getName()))
			{
				return t;
			}
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<Integer,Template> getTemplates() {
		return templates;
	}
	
	public void addTemplate(@SuppressWarnings("rawtypes") Template template) {
		templates.put(template.ID, template);
	}
	
	public Set<Integer> getUpgrades() {
		return upgrades;
	}
	
	public int getCurrentResourceAmount(ResourceType type) {
		Integer amount = currentResources.get(type);
		if(amount != null)
		{
			return amount;
		}
		else
		{
			return 0;
		}
	}
	
	public void setCurrentResourceAmount(ResourceType type, int amount) {
		currentResources.put(type, amount);
	}
	
	public void addToCurrentResourceAmount(ResourceType type, int increase) {
		setCurrentResourceAmount(type, getCurrentResourceAmount(type) + increase);
	}
	
	public int getCurrentSupply() {
		return currentSupply;
	}
	
	public void setCurrentSupply(int supply) {
		currentSupply = supply;
	}
	
	public void addToCurrentSupply(int increase) {
		setCurrentSupply(getCurrentSupply() + increase);
	}
	
	public int getCurrentSupplyCap() {
		return currentSupplyCap;
	}
	
	public void setCurrentSupplyCap(int supply) {
		currentSupplyCap = supply;
	}

	public void addToCurrentSupplyCap(int increase) {
		setCurrentSupplyCap(getCurrentSupplyCap() + increase);
	}
	
	public StateView getView() {
		return view;
	}
	
	public void setStateView(StateView view) {
		this.view = view;
	}
	
	
	public int[][] getVisibilityMatrix() {
		return canSee;
	}
	
	public void setVisibilityMatrix(int[][] matrix) {
		canSee = matrix;
	}

	@Override
	public boolean deepEquals(Object other) {
		//Doesn't check view because stateview is backed by state, so it would loop
		
		if (other == null || !this.getClass().equals(other.getClass()))
			return false;
		PlayerState o = (PlayerState)other;
		
		if (this.playerNum != o.playerNum)
			return false;
		{
			boolean thisnull = this.units == null;
			boolean othernull = o.units == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.units.size() != o.units.size())
					return false;
				for (Integer i : units.keySet())
				{
					if (!units.get(i).deepEquals(o.units.get(i)))
						return false;
				}
			}
		}
		
		{
			boolean thisnull = this.templates == null;
			boolean othernull = o.templates == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.templates.size() != o.templates.size())
					return false;
				for (Integer i : templates.keySet())
				{
					if (!templates.get(i).deepEquals(o.templates.get(i)))
						return false;
				}
			}
		}
		{
			boolean thisnull = this.upgrades == null;
			boolean othernull = o.upgrades == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.upgrades.size() != o.upgrades.size())
					return false;
				for (Integer i : upgrades)
				{
					if (!o.upgrades.contains(i))
						return false;
				}
			}
		}
		{
			for (ResourceType rt : ResourceType.values())
			{
				if (this.getCurrentResourceAmount(rt) != o.getCurrentResourceAmount(rt))
					return false;
			}
		}
		if (this.currentSupply != o.currentSupply)
			return false;
		if (this.currentSupplyCap != o.currentSupplyCap)
			return false;
		
		{
			boolean thisnull = this.canSee == null;
			boolean othernull = o.canSee == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.canSee.length != o.canSee.length)
					return false;
				for (int i = 0; i < this.canSee.length; i++)
				{
					if (this.canSee[i].length != o.canSee[i].length)
						return false;
					for (int j = 0; j < this.canSee[i].length; j++)
					{
						if (this.canSee[i][j]!=o.canSee[i][j])
							return false;
					}
				}
			}
		}
		return true;
	}
	
	
	
	//Removed because it seems unused and preserves references
//	@Override
//	protected Object clone() {
//		PlayerState copy = new PlayerState(playerNum);
//		for(Unit u : units.values())
//		{
//			copy.addUnit(u);
//		}
//		for(@SuppressWarnings("rawtypes") Template t : templates.values())
//		{
//			copy.addTemplate(t);
//		}
//		for(Integer i : upgrades)
//		{
//			copy.getUpgrades().add(i);
//		}
//		for(ResourceType type : currentResources.keySet())
//		{
//			copy.setCurrentResourceAmount(type, currentResources.get(type));
//		}
//		copy.setCurrentSupply(currentSupply);
//		copy.setCurrentSupplyCap(currentSupplyCap);
//		return copy;
//	}
//	
//	public PlayerState copyOf() {
//		return (PlayerState)clone();
//	}
	
}
