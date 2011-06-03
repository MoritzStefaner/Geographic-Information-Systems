package org.gis.db;

public class Party {

	private int firstVote;
	private int secondVote;
	private String name;
	
	public Party(String name, int firstVote, int secondVote){
		this.firstVote = firstVote;
		this.secondVote = secondVote;
		this.name = name;
	}

	public int getErststimme() {
		return firstVote;
	}

	public int getZweitstimmen() {
		return secondVote;
	}

	public String getName() {
		return name;
	}
}
