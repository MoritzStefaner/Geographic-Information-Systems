package org.gis.db;

public class Party {

	private Integer firstVote;
	private Integer secondVote;
	private String name;
	
	public Party(String name, Integer firstVote, Integer secondVote){
		this.firstVote = firstVote;
		this.secondVote = secondVote;
		this.name = name;
	}

	public Integer getErststimme() {
		return firstVote;
	}

	public Integer getZweitstimmen() {
		return secondVote;
	}

	public String getName() {
		return name;
	}
}
