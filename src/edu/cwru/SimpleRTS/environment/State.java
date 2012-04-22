package edu.cwru.SimpleRTS.environment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import edu.cwru.SimpleRTS.agent.Agent;
import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.Upgrade;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.util.DeepEquatable;

public class State implements Serializable, Cloneable, IDDistributer, DeepEquatable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	
	private int nextIDTarget;
	private int nextIDTemplate;
		@Override
	public int nextTargetID() {
		return nextIDTarget++;
	}
	@Override
	public int nextTemplateID() {
		return nextIDTemplate++;
	}
	/**
	 * DON'T USE THIS IF YOU AREN'T THE XML SAVING MECHANISM
	 * This must be different from the other so saves in the middle of an episode don't mess up the ids
	 * @return
	 */
	public int getNextTargetIDForXMLSave()
	{
		return nextIDTarget;
	}
	/**
	 * DON'T USE THIS IF YOU AREN'T THE XML SAVING MECHANISM
	 * This must be different from the other so saves in the middle of an episode don't mess up the ids
	 * @return
	 */
	public int getNextTemplateIDForXMLSave()
	{
		return nextIDTemplate;
	}
	
	//TODO: move this constant somewhere
	private final int MAXSUPPLY = 50;
	
	private Map<Integer,Unit> allUnits;
	//private Map<Integer, StateView> views;
	/**
	 * Maps player to an array of how many units can see each cell in the map
	 * Note that observer sight may not be used like the others
	 * Observer sight is tracked as the sum of all other sight
	 * But when it comes time to tell if the observer can see something, it always can
	 */
	//private Map<Integer,int[][]> playerCanSee;
	private boolean hasFogOfWar;
	//private Map<Integer,Map<Integer, Unit>> unitsByAgent;
	private List<ResourceNode> resourceNodes;
	private int turnNumber;
	//private Map<Pair<Integer,ResourceType>,Integer> currentResources;
	//private Map<Integer,Integer> currentSupply;
	//private Map<Integer,Integer> currentSupplyCap;
	//private Map<Integer, Set<Integer>> upgradesByAgent;
	private int xextent;
	private int yextent;
	//@SuppressWarnings("rawtypes")
	//private Map<Integer, Map<Integer,Template>> templatesByAgent;
	@SuppressWarnings("rawtypes")
	private Map<Integer,Template> allTemplates;
	//private HashMap<Integer, EventLogger> eventlogs;
	private Map<Integer,PlayerState> playerStates;
	private PlayerState observerState;
	@SuppressWarnings("rawtypes")
	public State() {
		nextIDTarget=0;
		nextIDTemplate=0;
		//playerCanSee = new HashMap<Integer,int[][]>();
		allUnits = new HashMap<Integer,Unit>();
		//unitsByAgent = new HashMap<Integer,Map<Integer,Unit>>();
		allTemplates = new HashMap<Integer,Template>();
		//templatesByAgent = new HashMap<Integer,Map<Integer,Template>>();
		//upgradesByAgent = new HashMap<Integer, Set<Integer>>();
		resourceNodes = new ArrayList<ResourceNode>();
		//currentResources = new HashMap<Pair<Integer,ResourceType>,Integer>();
		//currentSupply = new HashMap<Integer,Integer>();
		//currentSupplyCap = new HashMap<Integer,Integer>();
		//views = new HashMap<Integer,StateView>();
		//eventlogs = new HashMap<Integer, EventLogger>();
		//eventlogs.put(Agent.OBSERVER_ID, new EventLogger());
		playerStates = new HashMap<Integer,PlayerState>();
		//observerState = new PlayerState(Agent.OBSERVER_ID);
		addPlayer(Agent.OBSERVER_ID);
		setFogOfWar(false);
	}
	
	/**
	 * Used as a necessary part of loading from xml.
	 * Harmless but pointless to use otherwise.
	 */
	public void updateGlobalListsFromPlayers() {
		for(PlayerState player : playerStates.values())
		{
			for(Unit u : player.getUnits().values())
			{
				allUnits.put(u.ID, u);
			}
			for(@SuppressWarnings("rawtypes") Template t : player.getTemplates().values())
			{
				allTemplates.put(t.ID, t);
			}
		}
	}
//	Commented out because this is neither used nor functional
//	@Override
//	protected Object clone() {
//		State newstate = new State();
//		newstate.setSize(xextent, yextent);
//		/*for(Integer i : playerCanSee.keySet())
//		{
//			state.playerCanSee.put(i, playerCanSee.get(i).clone());
//		}	*/
//		for(Unit u : allUnits.values())
//		{//takes care of allUnits and unitsByAgent
//			Unit copy = u.copyOf();
//			newstate.addUnit(copy, copy.getxPosition(), copy.getyPosition());
//		}
//		newstate.allTemplates.putAll(allTemplates);
//		/*for(Integer i : templatesByAgent.keySet())
//		{
//			@SuppressWarnings("rawtypes")
//			Map<Integer,Template> templates = new HashMap<Integer,Template>();
//			templates.putAll(templatesByAgent.get(i));
//			state.templatesByAgent.put(i, templates);
//		}*//*
//		for(Integer i : upgradesByAgent.keySet())
//		{
//			Set<Integer> upgrades = new HashSet<Integer>();
//			upgrades.addAll(upgradesByAgent.get(i));
//			state.upgradesByAgent.put(i, upgradesByAgent.get(i));
//		}*/
//		for(ResourceNode node : resourceNodes)
//		{
//			newstate.resourceNodes.add(node.copyOf());
//		}
//		for(Integer i : playerStates.keySet())
//		{
//			newstate.playerStates.put(i, playerStates.get(i).copyOf());
//		}
//		//state.currentResources.putAll(currentResources);
//		//state.currentSupply.putAll(currentSupply);
//		//state.currentSupplyCap.putAll(currentSupplyCap);
//		
//		return newstate;
//	}
//	
//	public StateView getStaticCopy(int player) {
//		State state = (State)clone();
//		return state.getView(player);
//	}
	
	
	/**
	 * A deep equals method, because the equals methods of many classes have been hijacked into ID comparison for performance reasons
	 * @param other
	 * @return
	 */
	public boolean deepEquals(Object other)
	{
		
		if (other==null || !this.getClass().equals(other.getClass()))
			return false;
		
		State o = (State)other;
		if (this.MAXSUPPLY != o.MAXSUPPLY)
			return false;
		if (this.hasFogOfWar != o.hasFogOfWar)
			return false;
		if (this.nextIDTarget != o.nextIDTarget)
			return false;
		if (this.nextIDTemplate != o.nextIDTemplate)
			return false;
		if (this.xextent != o.xextent || this.yextent != o.yextent)
			return false;
		if (this.turnNumber != o.turnNumber)
			return false;
		
		{
			boolean thisnull = this.resourceNodes == null;
			boolean othernull = o.resourceNodes == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.resourceNodes.size() != o.resourceNodes.size())
					return false;
				for (int i = 0; i<this.resourceNodes.size();i++)
				{
					if (!resourceNodes.get(i).deepEquals(o.resourceNodes.get(i)))
						return false;
				}
			}
		}
		{
			boolean thisnull = this.allTemplates == null;
			boolean othernull = o.allTemplates == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.allTemplates.size() != o.allTemplates.size())
					return false;
				for (Integer i : allTemplates.keySet())
				{
					if (!allTemplates.get(i).deepEquals(o.allTemplates.get(i)))
						return false;
				}
			}
		}
		
		
		{
			boolean thisnull = this.allUnits == null;
			boolean othernull = o.allUnits == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.allUnits.size() != o.allUnits.size())
					return false;
				for (Integer i : allUnits.keySet())
				{
					if (!allUnits.get(i).deepEquals(o.allUnits.get(i)))
						return false;
				}
			}
		}
		{
			boolean thisnull = this.playerStates == null;
			boolean othernull = o.playerStates == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (this.playerStates.size() != o.playerStates.size())
					return false;
				for (Integer i : playerStates.keySet())
				{
					if (!playerStates.get(i).deepEquals(o.playerStates.get(i)))
						return false;
				}
			}
		}
		{
			boolean thisnull = this.observerState == null;
			boolean othernull = o.observerState == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (!this.observerState.deepEquals(o.observerState))
					return false;
			}
		}
		
		
		return true;
	}
	/**
	 * Return an array of the players currently in the game
	 * @return
	 */
	public Integer[] getPlayers()
	{
		return playerStates.keySet().toArray(new Integer[]{});
	}
	//@SuppressWarnings("rawtypes")
	/**
	 * Add another player
	 * @param playernumber The player number of the player to add
	 */
	public void addPlayer(int playernumber)
	{
		if (!playerStates.containsKey(playernumber) || playernumber == Agent.OBSERVER_ID)
		{
			PlayerState playerState = new PlayerState(playernumber);
			if(playernumber != Agent.OBSERVER_ID)
			{
				playerStates.put(playernumber, playerState);
			}
			else
			{
				observerState = playerState;
			}
			/*templatesByAgent.put(playernumber, new HashMap<Integer,Template>());
			unitsByAgent.put(playernumber, new HashMap<Integer,Unit>());
			upgradesByAgent.put(playernumber, new HashSet<Integer>());
			playerCanSee.put(playernumber, new int[getXExtent()][getYExtent()]);*/
			playerState.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
			
		}
	}
	
	public PlayerState getPlayerState(int player) {
		return playerStates.get(player);
	}
	
	public Collection<PlayerState> getPlayerStates() {
		return playerStates.values();
	}
	
	public int getTurnNumber() { return turnNumber; }
	public Map<Integer, Unit> getUnits() {
		return Collections.unmodifiableMap(allUnits);
	}
	public Unit getUnit(int unitId) {
		return allUnits.get(unitId);
	}
	public ResourceNode getResource(int resourceId) {
		for(ResourceNode r : resourceNodes)
		{
			if(resourceId == r.hashCode())
				return r;
		}
		return null;
	}
	public void addResource(ResourceNode resource) {
		resourceNodes.add(resource);
	}
	
	public void setFogOfWar(boolean fogofwar) {
		if (fogofwar)
		{
			hasFogOfWar = true;
//			recalculateVisionFromScratch();
		}
		else
		{
			hasFogOfWar = false;
		}
	}
	public boolean getFogOfWar()
	{
		return hasFogOfWar;
	}

	/**
	 * Returns whether the selected coordinates are visible to the player through the fog of war.
	 * @param x
	 * @param y
	 * @param player
	 * @return
	 */
	public boolean canSee(int x, int y, int player) {
		if (!hasFogOfWar || player == Agent.OBSERVER_ID)
		{
			return true;
		}
		if (!inBounds(x, y))
		{
			return false;
		}
		else
		{
			//int[][] cansee = playerCanSee.get(player);
			int[][] cansee = playerStates.get(player).getVisibilityMatrix();
			if (cansee==null)
			{
				return false;
			}
			else
			{
				return cansee[x][y]>0;
			}
		}
	}
	/**
	 * Recalculate the vision of each unit.  Required for xml loading, as that does not use the normal unit adding methods.
	 */
	public void forceRecalculateVision() {
		recalculateVisionFromScratch();
		
	}
	/**
	 * Recalculates the vision of each agent from scratch.
	 */
	private void recalculateVisionFromScratch() {
		observerState.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
		for (PlayerState player : playerStates.values())
		{
			player.setVisibilityMatrix(new int[getXExtent()][getYExtent()]);
		}
		for (Unit u : allUnits.values())
		{
			int player = u.getPlayer();
			PlayerState state = playerStates.get(player);
			int x = u.getxPosition();
			int y = u.getyPosition();
			int s = u.getTemplate().getSightRange();
			for (int i = x-s; i<=x+s;i++)
				for (int j = y-s; j<=y+s;j++)
					if (inBounds(i,j))
					{
						state.getVisibilityMatrix()[i][j]++;
						//playerCanSee.get(player)[i][j]++;
						//observersight[i][j]++;
						observerState.getVisibilityMatrix()[i][j]++;
					}
		}
	}
	/**
	 * Find the closest unoccupied position using a spiraling out search pattern
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] getClosestPosition(int x, int y)
	{
		//if the space in question is already open
		if (positionAvailable(x,y))
			return new int[]{x,y};
		int maxradius = Math.max(Math.max(x, xextent-x), Math.max(y,yextent-y));
		for (int r = 1; r<=maxradius;r++)
		{
			//go up/left diagonal
			x = x-1;
			y = y-1;
			
			//go down
			for (int i = 0; i<2*r;i++) {
				y = y + 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go right
			for (int i = 0; i<2*r;i++) {
				x = x + 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go up
			for (int i = 0; i<2*r;i++) {
				y = y - 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
			//go left
			for (int i = 0; i<2*r;i++) {
				x = x - 1;
				if (positionAvailable(x,y))
					return new int[]{x,y};
			}
		}
		return new int[]{-1,-1};
	}
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int templateId) {
		return allTemplates.get(templateId);
	}
	@SuppressWarnings("rawtypes")
	public Template getTemplate(int player, String name) {
		/*
		Map<Integer,Template> playerstemplates = templatesByAgent.get(player);
		if (playerstemplates == null)
		{
//			System.out.println("Player not found");
			return null;
		}
		for (Template t : playerstemplates.values()) {
			if (t.getName().equals(name))
			{
				return t;
			}
		}
		return null;*/
		PlayerState playerState = playerStates.get(player);
		if (playerState == null)
		{
			System.out.println("Player not found");
			return null;
		}		
		return playerState.getTemplate(name);
	}
	@SuppressWarnings("rawtypes")
	public Map<Integer,Template> getTemplates(int player) {
		/*if(templatesByAgent.get(player) == null)
			return null;
		return Collections.unmodifiableMap(templatesByAgent.get(player));*/
		if(playerStates.get(player) == null)
			return null;
		return Collections.unmodifiableMap(playerStates.get(player).getTemplates());
	}
	public boolean doesPlayerHaveUnit(int player, int templateid) {
		/*Map<Integer, Template> templates = templatesByAgent.get(player);
		if (templates != null) {
			return templates.containsKey(templateid);
		}
		
		return false;*/
		if(playerStates.get(player) != null)
		{
			return playerStates.get(player).getTemplate(templateid) != null;
		}
		return false;
	}
	public void setSize(int x, int y) {
		xextent = x;
		yextent = y;
		recalculateVisionFromScratch();
	}
	
	public String getTextString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i<xextent;i++)
		{
			str.append('|');
			for (int j = 0; j < yextent; j++)
			{
				Unit u = unitAt(i,j);
				if (u!=null)
				{//if there is a unit there
					str.append(u.getCharacter());
				}
				else
				{
					ResourceNode r = resourceAt(i, j);
					if (r != null)
					{
						str.append('0');
					}
					else
					{
						str.append(' ');
					}
					
				}
				str.append('|');
			}
			str.append('\n');
		}
		return str.toString();
	}
	private static final Map<Integer,Unit> EMPTY_MAP = new HashMap<Integer,Unit>();
	public Map<Integer,Unit> getUnits(int player) {
		//if(unitsByAgent.get(player) == null)
		if(playerStates.get(player) == null)
			return EMPTY_MAP;
		//return Collections.unmodifiableMap(unitsByAgent.get(player));
		return Collections.unmodifiableMap(playerStates.get(player).getUnits());
	}
	public boolean tryProduceUnit(Unit u,int x, int y) {
			if (!positionAvailable(x,y))
				return false;
			UnitTemplate ut = u.getTemplate();
			//Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
			//Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
			//Integer currentgold = currentResources.get(goldpair);
			Integer currentgold = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.GOLD);
			//Integer currentwood = currentResources.get(woodpair);
			Integer currentwood = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.WOOD);
			if (currentgold == null)
				currentgold = 0;
			if (currentwood == null)
				currentwood = 0;
			if (currentgold >= ut.getGoldCost() && currentwood >= ut.getWoodCost() && checkValidSupplyAddition(ut.getPlayer(), ut.getFoodCost(),ut.getFoodProvided()))
			{
				reduceResourceAmount(ut.getPlayer(), ResourceType.GOLD, u.getTemplate().getGoldCost());
				reduceResourceAmount(ut.getPlayer(), ResourceType.WOOD, u.getTemplate().getWoodCost());
				addUnit(u, x, y);
				return true;
			}
			else
			{
				return false;
			}
	}
	public void addUnit(Unit u,int x, int y) {
		
		int player = u.getPlayer();
		if (!playerStates.containsKey(player))
			addPlayer(player);
		if(!allUnits.containsKey(u.ID)) {
			//Map<Integer, Unit> map = unitsByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			if(playerState == null)
			{
//				TODO: see if we can do without this
				addPlayer(player);
			}
			Map<Integer, Unit> map = playerState.getUnits();
			/*if(map == null)
			{
				unitsByAgent.put(player, map = new HashMap<Integer, Unit>());
			}*/
			allUnits.put(u.ID,u);
			map.put(u.ID, u);
			alterSupplyCapAmount(player,u.getTemplate().getFoodProvided());
			alterSupplyAmount(player, u.getTemplate().getFoodCost());
			u.setxPosition(x);
			u.setyPosition(y);
			int sightrange = u.getTemplate().getSightRange();
			for (int i = x-sightrange; i<= x+sightrange;i++)
				for (int j = y-sightrange; j<= y+sightrange;j++)
				{
					if (inBounds(i,j))
					{
						//playerCanSee.get(Agent.OBSERVER_ID)[i][j]++;
						observerState.getVisibilityMatrix()[i][j]++;
						//System.out.println(u);
						//System.out.println(u.getPlayer());
						//System.out.println(playerCanSee.entrySet());
						//playerCanSee.get(u.getPlayer())[i][j]++;
						playerState.getVisibilityMatrix()[i][j]++;
					}
				}
		}
		
	}
	/**
	 * Move a unit in a direction.
	 * Does not perform collision checks of any kind
	 * @param u
	 * @param direction
	 */
	public void moveUnit(Unit u, Direction direction) {
		int sightrange = u.getTemplate().getSightRange();
		int x = u.getxPosition();
		int y = u.getyPosition();
		//int[][] playersight=playerCanSee.get(u.getTemplate().getPlayer());
		int[][] playersight = playerStates.get(u.getPlayer()).getVisibilityMatrix();
		//int[][] observersight=playerCanSee.get(Agent.OBSERVER_ID);
		int[][] observersight = observerState.getVisibilityMatrix();
		
//		String s="";
//		for (int i = 0; i<getXExtent();i++)
//		{
//			for (int j = 0; j<getYExtent();j++)
//			{
//				s+="|"+playersight[i][j];
//				
//			}
//			s+="|\n";
//		}
//		System.out.println(s);
		if (direction.xComponent()!=0)
		{
			int xoffset;
			int xdirection;
			if (direction.xComponent() > 0 )
			{
				xdirection = 1;
				xoffset = direction.xComponent();
			}
			else
			{
				xdirection = -1;
				xoffset = -direction.xComponent();
			}
			for (int dist = 0; dist < xoffset; dist++)
			{
				
				int xtoadd = x + (sightrange - dist+1) * xdirection;
				int xtoremove = x - (sightrange + dist) * xdirection;
				for (int j = y - sightrange; j <= y + sightrange; j++)
				{
					if (inBounds(xtoadd,j))
					{
						
						playersight[xtoadd][j]++;
						observersight[xtoadd][j]++;
					}
					if (inBounds(xtoremove,j))
					{
						playersight[xtoremove][j]--;
						observersight[xtoremove][j]--;
					}
				}
				
			}
			//move along x
			u.setxPosition(x+direction.xComponent());
			//Get the new x
			x = u.getxPosition();
		}
		
		if (direction.yComponent()!=0)
		{
			int yoffset;
			int ydirection;
			if (direction.yComponent() > 0 )
			{
				ydirection = 1;
				yoffset = direction.yComponent();
			}
			else
			{
				ydirection = -1;
				yoffset = -direction.yComponent();
			}
			for (int dist = 0; dist < yoffset; dist++)
			{
				
				int ytoadd = y + (sightrange - dist+1) * ydirection;
				int ytoremove = y - (sightrange + dist) * ydirection;
				for (int i = x - sightrange; i <= x + sightrange; i++)
				{
					if (inBounds(i,ytoadd))
					{
						
						playersight[i][ytoadd]++;
						observersight[i][ytoadd]++;
					}
					if (inBounds(i,ytoremove))
					{
						playersight[i][ytoremove]--;
						observersight[i][ytoremove]--;
					}
				}
				
			}
			//move along y
			u.setyPosition(y+direction.yComponent());
			//Get the new y
			y = u.getyPosition();
		}
	}
	/**
	 * Move a unit from one place to another and update the sight radius.
	 * For use in the rare cases where a single direction is insufficient.
	 * @param u The unit to move
	 * @param newx The new x position of the unit
	 * @param newy The new y position of the unit
	 */
	public void transportUnit(Unit u, int newx, int newy)
	{
		int[][] playersight = playerStates.get(u.getPlayer()).getVisibilityMatrix();
		//int[][] observersight=playerCanSee.get(Agent.OBSERVER_ID);
		int[][] observersight = observerState.getVisibilityMatrix();
		int oldx = u.getxPosition();
		int oldy = u.getyPosition();
		int sightrange = u.getTemplate().getSightRange();
		for (int i = oldx-sightrange; i<= oldx+sightrange;i++)
			for (int j = oldy-sightrange; j<= oldy+sightrange;j++)
			{
				if (inBounds(i,j))
				{
					playersight[i][j]++;
					observersight[i][j]++;
				}
			}
		for (int i = newx-sightrange; i<= newx+sightrange;i++)
			for (int j = newy-sightrange; j<= newy+sightrange;j++)
			{
				if (inBounds(i,j))
				{
					playersight[i][j]++;
					observersight[i][j]++;
				}
			}
	}
	public void removeUnit(int unitID) {
		if (allUnits.containsKey(unitID))
		{
			Unit u = allUnits.remove(unitID);
			//unitsByAgent.get(u.getPlayer()).remove(unitID);
			playerStates.get(u.getPlayer()).getUnits().remove(unitID);
			alterSupplyCapAmount(u.getPlayer(),-u.getTemplate().getFoodProvided());
			alterSupplyAmount(u.getPlayer(), -u.getTemplate().getFoodCost());
			int x = u.getxPosition();
			int y = u.getyPosition();
			int sightrange = u.getTemplate().getSightRange();
			for (int i = x-sightrange; i<= x+sightrange;i++)
				for (int j = y-sightrange; j<= y+sightrange;j++)
				{
					if (inBounds(i,j))
					{
						//playerCanSee.get(Agent.OBSERVER_ID)[i][j]--;
						observerState.getVisibilityMatrix()[i][j]--;
						//playerCanSee.get(u.getPlayer())[i][j]--;
						playerStates.get(u.getPlayer()).getVisibilityMatrix()[i][j]--;
					}
				}
		}
		
	}
	@SuppressWarnings("rawtypes")
	public void addTemplate(Template t) {
		int player = t.getPlayer(); 
		if (!playerStates.containsKey(player))
			addPlayer(player);
		if(!allTemplates.containsKey(t.ID)) {
			//Map<Integer, Template> map = templatesByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			if(playerState == null)
			{
//				TODO: see if we can do without this
				addPlayer(player);
			}
			Map<Integer, Template> map = playerState.getTemplates();
			/*if(map == null)
			{
				templatesByAgent.put(player, map = new HashMap<Integer, Template>());
			}*/
			allTemplates.put(t.ID,t);
			map.put(t.ID, t);
		}
	}
	public boolean tryProduceUpgrade(Upgrade upgrade) {
		UpgradeTemplate ut = upgrade.getTemplate();
		//Pair<Integer,ResourceType> goldpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.GOLD);
		//Pair<Integer,ResourceType> woodpair = new Pair<Integer,ResourceType>(ut.getPlayer(),ResourceType.WOOD);
		//Integer currentgold = currentResources.get(goldpair);
		Integer currentgold = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.GOLD);
		//Integer currentwood = currentResources.get(woodpair);
		Integer currentwood = playerStates.get(ut.getPlayer()).getCurrentResourceAmount(ResourceType.WOOD);
		if (currentgold == null)
			currentgold = 0;
		if (currentwood == null)
			currentwood = 0;
		if (currentgold >= ut.getGoldCost() && currentwood >= ut.getWoodCost())
		{
			reduceResourceAmount(ut.getPlayer(), ResourceType.GOLD, ut.getGoldCost());
			reduceResourceAmount(ut.getPlayer(), ResourceType.WOOD, ut.getWoodCost());
			addUpgrade(upgrade);
			return true;
		}
		else
		{
			return false;
		}
	}
	private void addUpgrade(Upgrade upgrade) {
			UpgradeTemplate upgradetemplate = upgrade.getTemplate();
			int player = upgradetemplate.getPlayer();
			//Set<Integer> list = upgradesByAgent.get(player);
			PlayerState playerState = playerStates.get(player);
			Set<Integer> list = playerState.getUpgrades();
			//make sure it isn't already upgraded
			if (!list.contains(upgradetemplate.ID))
			{
				//upgrade all of the affected units
				for (UnitTemplate toupgrade : upgradetemplate.getAffectedUnits()) {
					toupgrade.setPiercingAttack(toupgrade.getPiercingAttack() + upgradetemplate.getPiercingAttackChange());
					toupgrade.setBasicAttack(toupgrade.getBasicAttack() + upgradetemplate.getBasicAttackChange());
					toupgrade.setArmor(toupgrade.getArmor() + upgradetemplate.getArmorChange());
					//If there is a health change, you need to update all of the units with that template
					if (upgradetemplate.getHealthChange() != 0)
					{
						//TODO: properly handle the cases where things go negative
						toupgrade.setBaseHealth(toupgrade.getBaseHealth()+upgradetemplate.getHealthChange());
						for (Unit u : allUnits.values()) { //check all units, not just that player's, just to be safe
							if (toupgrade.equals(u.getTemplate())) {
								u.setHP(u.getCurrentHealth() + upgradetemplate.getHealthChange());
							}
						}
					}
					toupgrade.setRange(toupgrade.getRange() + upgradetemplate.getRangeChange());
					toupgrade.setSightRange(toupgrade.getSightRange() + upgradetemplate.getSightRangeChange());
				}
				if (upgradetemplate.getSightRangeChange() != 0)
				{
					forceRecalculateVision();
				}
			}
			list.add(upgradetemplate.ID);
	}
	public boolean hasUpgrade(Integer upgradetemplateid, int player) {
		/*Set<Integer> set = upgradesByAgent.get(player);
		if(set == null)
		{
			return false;
		}
		return set.contains(upgradetemplateid);*/
		return playerStates.get(player).getUpgrades().contains(upgradetemplateid);
	}
	public List<ResourceNode> getResources() {
		return Collections.unmodifiableList(resourceNodes);
	}
	public void removeResourceNode(int resourceID) {
		for (int i = 0; i<resourceNodes.size();i++) {
			if (resourceNodes.get(i).ID == resourceID) {
				resourceNodes.remove(i);
				break;
			}
		}
	}
	public boolean positionAvailable(int x, int y)
	{
		return inBounds(x,y) && unitAt(x,y)==null && resourceAt(x,y)==null;
		
	}
	public Unit unitAt(int x, int y) {
		//This could probably be replaced by a 2D boolean array, but then you would need to ensure that things can't move without changing that array 
		for(Unit u : allUnits.values()) {
			if(u.getxPosition() == x && u.getyPosition() == y)
				return u;
		}
		return null;
	}
	public boolean inBounds(int x, int y)
	{
		return x>=0 && y>=0 && x<xextent && y<yextent; 
	}
	public int getXExtent() {
		return xextent;
	}
	public int getYExtent() {
		return yextent;
	}
	public ResourceNode resourceAt(int x, int y) {
		for(ResourceNode r : resourceNodes)
		{
			if(r.getxPosition() == x && r.getyPosition() == y)
				return r;
		}
		return null;
	}
	public int getResourceAmount(int player, ResourceType type) {
		//Integer amount = currentResources.get(new Pair<Integer,ResourceType>(player,type));
		Integer amount = playerStates.get(player).getCurrentResourceAmount(type);
		return amount != null ? amount : 0;			
	}
	public void depositResources(int player, ResourceType type, int amount)
	{
		if (amount > 0)
		{
			addResourceAmount(player, type, amount);
		}
	}
	/**
	 * Adds an amount of a resource to a player's global amount.
	 * @param player
	 * @param type
	 * @param amount
	 */
	private void addResourceAmount(int player, ResourceType type, int amount) {
		/*Pair<Integer,ResourceType> pair = new Pair<Integer,ResourceType>(player,type);
		Integer previous = currentResources.get(pair);
		if(previous == null)
			previous = 0;
		currentResources.put(pair, previous+amount);*/
		playerStates.get(player).addToCurrentResourceAmount(type, amount);
	}
	/**
	 * Attempts to reduce the player's amount of the given resource by an amount.
	 * If the player does not have enough of that resource, the transaction fails.
	 * @param player
	 * @param type
	 * @param amount
	 */
	private void reduceResourceAmount(int player, ResourceType type, int amount) {
		addResourceAmount(player, type, -amount);
	}
	public int getSupplyAmount(int player) {
		/*Integer amount = currentSupply.get(player);
		return amount != null ? amount : 0;*/
		return playerStates.get(player).getCurrentSupply();
	}
	public int getSupplyCap(int player) {
		/*Integer amount = currentSupplyCap.get(player);
		return Math.min(amount != null ? amount : 0,MAXSUPPLY);*/
		return Math.min(playerStates.get(player).getCurrentSupplyCap(), MAXSUPPLY);
	}
	
	/**
	 * Reduce the supply cap of a player (EG: when a farm dies)
	 * @param player
	 * @param amount
	 */
	private void alterSupplyCapAmount(int player, int amount) {
		/*Integer i = currentSupplyCap.get(player);
		if(i == null) //this should never happen
			i=0;
		currentSupplyCap.put(player, i+amount);*/
		playerStates.get(player).addToCurrentSupplyCap(amount);
	}
	/**
	 * Consume some of the supply
	 * @param player
	 * @param amount
	 */
	private void alterSupplyAmount(int player, int amount) {
		/*
		Integer currentsupply = currentSupply.get(player);
		if (currentsupply == null)
			currentsupply = 0;
		
		currentSupply.put(player, currentsupply+amount);*/
		playerStates.get(player).addToCurrentSupply(amount);
	}
	public boolean checkValidSupplyAddition(int player, int amounttoadd, int offsettingcapgain) {
		if (amounttoadd<=0)
		{
			//it is always valid to make something that takes no or negative supply
			return true;
		}
		else
		{
			/*Integer currentcap = currentSupplyCap.get(player);
			if (currentcap == null)
			{
				currentcap = 0; //just set it to zero if it isn't set, this way it functions right if for some reason we make it possible to be using a negative amount of supply
				//set it, because why not
				currentSupplyCap.put(player, 0);
			}
			Integer currentsupply = currentSupply.get(player);
			if (currentsupply == null)
			{
				currentsupply = 0;
				//set it, because why not
				currentSupply.put(player, 0);
			}
			return Math.min(currentcap+ offsettingcapgain, MAXSUPPLY) >= currentsupply + amounttoadd;*/
			
			PlayerState playerState = playerStates.get(player);
			return Math.min(playerState.getCurrentSupplyCap() + offsettingcapgain, MAXSUPPLY) >=
						    playerState.getCurrentSupply() + amounttoadd;
			 
		}
	}
	/**
	 * Go to the next turn.
	 * Increases the turn number and tells the logs to go to the next turn
	 */
	public void incrementTurn() {
		turnNumber++;
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Builder class that allows one-time access to a new state for construction purposes.
	 * @author Tim
	 *
	 */
	public static class StateBuilder {
		private State state;
		private boolean built;
		public StateBuilder() {
			state = new State();
			built = false;
		}
		public void setIDDistributerTemplateMax(int newmax)
		{
			state.nextIDTemplate=newmax;
		}
		public void setIDDistributerTargetMax(int newmax)
		{
			state.nextIDTarget=newmax;
		}
		public void addPlayer(PlayerState player) {
			state.playerStates.put(player.playerNum, player);
		}
		public void addUnit(Unit u, int x, int y) {
			state.addUnit(u,x,y);
		}
		public void addTemplate(Template t) {
			state.addTemplate(t);
		}
		public Template getTemplate(int player, String name)
		{
			return state.getTemplate(player, name);
		}
		public void setSize(int x, int y) {
			state.setSize(x, y);
		}
		public boolean positionAvailable(int x, int y) {
			return state.positionAvailable(x, y);
		}
		public void addResource(ResourceNode r) {
			if(!state.resourceNodes.contains(r))
				state.resourceNodes.add(r);
		}
		public void setTurn(int turn) {
			state.turnNumber = turn;
		}
		public void setResourceAmount(int player, ResourceType resource, int amount) {
			state.addResourceAmount(player, resource, amount);
		}
		public void setSupplyCap(int player, int supply) {
			//state.currentSupplyCap.put(player, supply);
			state.alterSupplyCapAmount(player, supply);
		}
		public String getTextString() {
			return state.getTextString();
		}
		public boolean hasTemplates(int player) {
			//Map<Integer,Template> templates = state.templatesByAgent.get(player);
			if(state.getPlayerState(player) == null)
			{
				return false;
			}
			Map<Integer,Template> templates = state.getPlayerState(player).getTemplates();
			/*if (templates == null) {
				return false;
			}*/
			return templates.size() != 0;
		}
		/**
		 * Completes construction of the state and returns a reference to the state.
		 * Subsequent calls to this method will result in returning null.
		 * @return - the state being built if this is the first call for this object, null otherwise
		 */
		public State build() {
			if(!built)
			{
				built = true;
				return state;
			}
			else
				return null;
		}
		public boolean closed() {
			return built;
		}
	}
	public StateView getView(int player) {
		//StateView toreturn = views.get(player);
		if(player == Agent.OBSERVER_ID)
		{
			if(observerState.getView() == null)
				observerState.setStateView(new StateView(this,player));

			return observerState.getView();
		}
		
		StateView toreturn = playerStates.get(player).getView();
		if(toreturn == null)
		{
			toreturn = new StateView(this,player);
			//views.put(player, toreturn);
			playerStates.get(player).setStateView(toreturn);
		}
		return toreturn;
	}
	@SuppressWarnings("rawtypes")
	/**
	 * Provides a read-only view of class values
	 * @author Tim
	 *
	 */
	public static class StateView implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private State state;
		private int player;
		private StateView(State state, int player) {
			this.state = state;
			this.player = player;
		}
//		commented out because it is neither used nor functional
//		public StateView getStaticCopy() {
//			return state.getStaticCopy(player);
//		}
		
		
		
		/**
		 * If the player can see the whole state, get a StateCreator that will rebuild the state as it is when this is called.<br/>
		 * This can be used as an immutable and repeatable deep copy of the underlying state.
		 * @return When fog of war is on and the player is not an observer: null;<br/>otherwise: A StateCreator that rebuilds the underlying state.
		 * @throws IOException
		 */
		public StateCreator getStateCreator() throws IOException {
			if (!state.hasFogOfWar || player == Agent.OBSERVER_ID)
			{
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				ObjectOutputStream o = new ObjectOutputStream(bos);
				o.writeObject(state);
				o.flush();
				o.close();
				byte[] stateData = bos.toByteArray();
				bos.close();
				return new RawStateCreator(stateData);
			}
			else
			{
				return null;
			}
		}
		/**
		 * Get all of the unit ids that you can see
		 * @return
		 */
		public List<Integer> getAllUnitIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Entry<Integer, Unit> e: state.allUnits.entrySet())
				if (canSee(e.getValue().getxPosition(), e.getValue().getyPosition()))
					ids.add(e.getKey());
			return ids;
		}
		/**
		 * Get whether fog of war (partial observability) is activated
		 * @return
		 */
		public boolean isFogOfWar() {
			return state.getFogOfWar();
		}
		/**
		 * Get the list of the player numbers of players in the map.
		 * @return
		 */
		public Integer[] getPlayerNumbers() {
			//Make a copy of the real list
			Integer[] actualPlayers = state.getPlayers();
			Integer[] safePlayers = new Integer[actualPlayers.length];
			System.arraycopy(actualPlayers, 0, safePlayers, 0, actualPlayers.length);
			return safePlayers;
		}
		/**
		 * Returns whether the selected coordinates are visible to the player through the fog of war.
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean canSee(int x, int y)
		{
			return state.canSee(x,y,player);
		}
		public int getTurnNumber()
		{
			return state.getTurnNumber();
		}
		/**
		 * Get the unit ids of those units owned by the selected players.
		 * Will give only your units when fog of war is on, unless you are an observer
		 * @param player
		 * @return
		 */
		public List<Integer> getUnitIds(int player) {
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Unit> units = state.getUnits(player);
			if(units != null)
				for(Entry<Integer, Unit> e: units.entrySet())
				{
					if (canSee(e.getValue().getxPosition(), e.getValue().getyPosition()))
						ids.add(e.getKey());
				}
			return ids;
		}
		/**
		 * Get the unit with the selected id if you can see it.
		 * @param unitID
		 * @return The unit with that ID, null if you can't see it or if it doesn't exist
		 */
		public Unit.UnitView getUnit(int unitID) {
			Unit u = state.getUnit(unitID);
			if (u==null)
				return null;
			if (!canSee(u.getxPosition(),u.getyPosition()))
				return null;
			return u.getView();
		}
		/**
		 * Get the IDs of all of the resource nodes that you can see
		 * @return
		 */
		public List<Integer> getAllResourceIds() {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				if (canSee(r.getxPosition(),r.getyPosition()))
					i.add(r.getID());
			return i;
		}
		/**
		 * Get the IDs of all of the resource nodes of a type that you can see
		 * @param type Gold mine or Tree
		 * @return
		 */
		public List<Integer> getResourceNodeIds(ResourceNode.Type type) {
			List<Integer> i = new ArrayList<Integer>();
			for(ResourceNode r : state.resourceNodes)
				if (r.getType() == type && canSee(r.getxPosition(), r.getyPosition()))
					i.add(r.getID());
			return i;
		}
		
		/**
		 * Get the resource node with the selected ID (if you can see it)
		 * @param resourceID
		 * @return The resource node with id resourceID, or null if you can't see it or if there isn't one.
		 */
		public ResourceNode.ResourceView getResourceNode(int resourceID) {
			ResourceNode r =state.getResource(resourceID);
			if (r==null)
				return null;
			if (!canSee(r.getxPosition(),r.getyPosition()))
				return null;
			return state.getResource(resourceID).getView();
		}
		/**
		 * Get the ids of all the templates.
		 * If you are not an observer, it will only give you yours if fog of war is on
		 * @return
		 */
		public List<Integer> getAllTemplateIds() {
			List<Integer> ids = new ArrayList<Integer>();
			for(Entry<Integer, Template> e : state.allTemplates.entrySet())
			{
				if (!state.hasFogOfWar || e.getValue().getPlayer() == player || player == Agent.OBSERVER_ID)
					ids.add(e.getKey());
			}
			return ids;
		}
		
		/**
		 * Get a player's template IDs
		 * If you are not an observer, you can't get other people's templates if fog of war is on
		 * @param playerid
		 * @return
		 */
		public List<Integer> getTemplateIds(int playerid) {
			if (state.hasFogOfWar && playerid != player && player != Agent.OBSERVER_ID)
				return null;
			List<Integer> ids = new ArrayList<Integer>();
			Map<Integer, Template> templates = state.getTemplates(playerid);
			if(templates != null)
				for(Integer key : templates.keySet())
				{
					ids.add(key);
				}
			return ids;
		}
		
		/**
		 * Get a template with a specific ID
		 * If you are not an observer, it won't work with somebody else's template with fog of war on
		 * @param templateID
		 * @return
		 */
		public Template.TemplateView getTemplate(int templateID) {
			Template template = state.getTemplate(templateID);
			if (template == null)
				return null;
			if (state.hasFogOfWar && player != template.getPlayer() && player != Agent.OBSERVER_ID)
				return null;
			return template.getView();
		}
		/**
		 * Get a template with that name owned by that player 
		 * @param player
		 * @param name
		 * @return The view of the first (and what should be the only) template that has the specified name, or null if that player does not have a template by that name
		 */
		public Template.TemplateView getTemplate(int player, String name) {
			Template t = state.getTemplate(player,name);
			if (t!=null)
				return t.getView();
			else //if it is null
				return null;
		}
		/**
		 * Get the amount of wood or gold (specified by the type) available to a specific player.
		 * If you are not an observer, it will not work on other people with fog of war on
		 * @param player
		 * @param type The type of resource
		 * @return
		 */
		public Integer getResourceAmount(int playerid, ResourceType type) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getResourceAmount(playerid, type);	
		}
		
		/**
		 * Get the amount of supply (food) used by a specific player.
		 * If you are not an observer, it will not work on other people with fog of war on
		 * @param player
		 * @return
		 */
		public Integer getSupplyAmount(int playerid) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getSupplyAmount(playerid);
		}
		/**
		 * Get the maximum amount of supply (food) available to a specific player.
		 * If you are not an observer, it will not work on other people with fog of war on
		 * @param player
		 * @return
		 */
		public Integer getSupplyCap(int playerid) {
			if (state.hasFogOfWar && player != playerid && player != Agent.OBSERVER_ID)
				return null;
			return state.getSupplyCap(playerid);
		}
		/**
		 * Gets the closest position that you can see.
		 * @param x
		 * @param y
		 * @return
		 */
		public int[] getClosestOpenPosition(int x, int y) {
			
			return state.getClosestPosition(x, y);
		}
		/**
		 * Get how big the map is in the x direction
		 * @return
		 */
		public int getXExtent() {
			return state.getXExtent();
		}
		/**
		 * Get how big the map is in the y direction
		 * @return
		 */
		public int getYExtent() {
			return state.getYExtent();
		}
		/**
		 * Get whether a player has a unit of a certain type.  (Say, a tech building).
		 * If you are not an observer, then this will not work on other players with fog of war on
		 * @param player
		 * @param buildingtemplateid
		 * @return Whether the player with id playerid has a unit with a template with the template id templateid, or false if the player is not you
		 */
		public boolean doesPlayerHaveUnit(int playerid, int templateid) {
			if (state.hasFogOfWar && playerid!=this.player && this.player != Agent.OBSERVER_ID)
				return false;
			return state.doesPlayerHaveUnit(playerid, templateid);
		}
		/**
		 * Get whether a player has researched a specific upgrade.
		 * If you are not an observer, then this will not work on other players with fog of war on
		 * @param upgradeid
		 * @param playerid
		 * @return Whether the player has researched an upgrade with id upgradeid.  Always false if you try it on someone else.
		 */
		public boolean hasUpgrade(int upgradeid, int playerid) {
			if (state.hasFogOfWar && playerid!=this.player && this.player != Agent.OBSERVER_ID)
				return false;
			return state.hasUpgrade(upgradeid, playerid);
		}
		
		/**
		 * Find whether a position is in bounds.
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean inBounds(int x, int y) {
			return state.inBounds(x, y);
		}
		
		/**
		 * Find if there is a unit at a position
		 * @param x
		 * @param y
		 * @return Whether there is a unit at the position (always false if you can't see there)
		 */
		public boolean isUnitAt(int x, int y) {
			if (!canSee(x, y))
				return false;
			return state.unitAt(x, y) != null;
		}
		/**
		 * Get the unit at a position
		 * @param x
		 * @param y
		 * @return The unit's ID, or null if there is no unit (or if you can't see there)
		 */
		public Integer unitAt(int x, int y) {
			if (!canSee(x, y))
				return null;
			Unit unit = state.unitAt(x,y);
			return unit==null?null:unit.ID;
		}
		/**
		 * Find whether there is a resource at the position
		 * @param x
		 * @param y
		 * @return Whether there is a resource there (always false if you can't see there)
		 */
		public boolean isResourceAt(int x, int y) {
			if (!canSee(x, y))
				return false;
			return state.resourceAt(x, y) != null;
		}
		/**
		 * Get the resource at a position
		 * @param x
		 * @param y
		 * @return The resource's ID, or null if there is no resource or if you can't see the position
		 */
		public Integer resourceAt(int x, int y) {
			if (!canSee(x, y))
				return null;
			ResourceNode resource = state.resourceAt(x,y);
			return resource==null?null:resource.ID;
		}
		
	}
	
	


	
	
}
