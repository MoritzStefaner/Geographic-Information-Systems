package org.gis.db;

import java.util.LinkedList;

public class FederalState {

	private int id;
	private String name;
	int electorate;
	int voter;
	private LinkedList<Party> result;
	private LinkedList<Constituency> constituencies;
	
	public FederalState(int id, String name, int electorate, int voter, LinkedList<Party> result){
		this.id = id;
		this.name = name;
		this.electorate = electorate;
		this.voter = voter;
		this.result = result;
		this.constituencies = new LinkedList<Constituency>();
	}
	
	public LinkedList<Constituency> getConstituencies(){
		return this.constituencies;
	}
	
	public void addConstituency(Constituency constituency){
		constituencies.add(constituency);
	}

	public int getId() {
		return id;
	}

	public int getElectorate() {
		return electorate;
	}

	public int getVoter() {
		return voter;
	}

	public LinkedList<Party> getElectionResult() {
		return result;
	}

	public String getName() {
		return name;
	}
}
