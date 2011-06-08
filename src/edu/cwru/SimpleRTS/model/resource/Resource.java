package edu.cwru.SimpleRTS.model.resource;

import edu.cwru.SimpleRTS.model.unit.Unit;

public class Resource {
	private static int nextID = 0;
	private Type type;
	private int xPosition;
	private int yPosition;
	private final int ID;
	
	public Resource(Type type, int xPosition, int yPosition) {
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		ID = nextID++;
	}
	
	public Type getType() {
		return type;
	}

	public int getxPosition() {
		return xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public int getID() {
		return ID;
	}

	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Resource))
			return false;
		return ((Resource)o).ID == ID;
	}
	
	public enum Type { TREE, GOLD_MINE };
}
