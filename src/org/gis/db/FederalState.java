package org.gis.db;

public class FederalState {

	private int id;
	private String name;
	
	public FederalState(int id, String name){
		this.id = id;
		this.name = name;
	}

	protected int getId() {
		return id;
	}

	protected String getName() {
		return name;
	}
}
