package org.gis.db;

import java.util.*;

public class Constituency {

	int number;
	String name;
	int electorate;
	int voter;
	private FederalState federalState;
	private LinkedList<Party> result;
	private LinkedList<ConstPolygon> polygons;
	
	public Constituency(int number, String name, int electorate, int voter, FederalState federalState, LinkedList<Party> result, LinkedList<ConstPolygon> polygons) {
		this.number = number;
		this.name = name;
		this.electorate = electorate;
		this.voter = voter;
		this.federalState = federalState;
		this.result = result;
		this.polygons = polygons;
	}

	public int getElectorate() {
		return electorate;
	}

	public int getVoter() {
		return voter;
	}

	public LinkedList<Party> getResult() {
		return result;
	}

	public int getNumber() {
		return number;
	}

	public FederalState getFederalState() {
		return federalState;
	}

	public LinkedList<Party> getElectionResult() {
		return result;
	}

	public LinkedList<ConstPolygon> getPolygons() {
		return polygons;
	}

	public String getName() {
		return name;
	}

}
