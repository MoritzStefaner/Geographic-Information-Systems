package org.gis.db;

import java.util.LinkedList;

public class FederalState {

	private Integer id;
	private String name;
	private Integer electorate;
	private Integer voter;
	private LinkedList<Party> result;
	private LinkedList<Constituency> constituencies;
	
	public FederalState(Integer id, String name, Integer electorate, Integer voter, LinkedList<Party> result){
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

	public Integer getId() {
		return id;
	}

	public Integer getElectorate() {
		return electorate;
	}

	public Integer getVoter() {
		return voter;
	}

	public LinkedList<Party> getElectionResult() {
		return result;
	}

	public String getName() {
		return name;
	}
}
