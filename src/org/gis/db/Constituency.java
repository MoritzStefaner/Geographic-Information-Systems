package org.gis.db;

import java.util.*;

import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class Constituency {
	int number;
	String name;
	int electorate;
	int voter;
	private FederalState federalState;
	private LinkedList<Party> result;
	private LinkedList<MapMarkerPolygon> polygons;
	
	public Constituency(int number, String name, int electorate, int voter, FederalState federalState, LinkedList<Party> result) {
		this.number = number;
		this.name = name;
		this.electorate = electorate;
		this.voter = voter;
		this.federalState = federalState;
		this.result = result;
		this.polygons = new LinkedList<MapMarkerPolygon>();
	}

	public void addPolygons(LinkedList<MapMarkerPolygon> list) {
		this.polygons = list;
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

	public LinkedList<MapMarkerPolygon> getPolygons() {
		return polygons;
	}

	public String getName() {
		return name;
	}

}
