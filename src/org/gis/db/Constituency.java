package org.gis.db;

import java.util.*;

public class Constituency {
	Integer number;
	String name;
	Integer electorate;
	Integer voter;
	private FederalState federalState;
	private LinkedList<Party> result;
	private LinkedList<ConstPolygon> polygons;
	private int malteOccurence;
	
	public Constituency(Integer number, String name, Integer electorate, Integer voter, FederalState federalState, LinkedList<Party> result) {
		this.number = number;
		this.name = name;
		this.electorate = electorate;
		this.voter = voter;
		this.federalState = federalState;
		this.result = result;
		this.malteOccurence = 0;
	}

	public void addMalteOccurence(int add) {
		malteOccurence += add;
	}
	
	public int getMalteOccurences() {
		return malteOccurence;
	}
	
	public void addPolygons(LinkedList<ConstPolygon> list) {
		this.polygons = list;
	}
	
	public int getElectorate() {
		return electorate;
	}

	public Integer getVoter() {
		return voter;
	}

	public LinkedList<Party> getResult() {
		return result;
	}

	public Integer getNumber() {
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

	public String getInformation() {
		String s = getName() + "\n";
		s = s + getFederalState().getName() + "\n\n";
		
		s = s + "Wahlberechtigte: " + getElectorate() + "\n";
		s = s + "Wähler: " + getVoter() + "\n\n";
		
		Iterator<Party> it = getResult().iterator();
		while (it.hasNext()) {
			Party p = it.next();
			s = s + p.getName() + ": " + p.getZweitstimmen() + "\n";
		}
		
		return s;
	}
}
