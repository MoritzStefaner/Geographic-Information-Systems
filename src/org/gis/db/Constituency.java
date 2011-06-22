package org.gis.db;

import java.util.*;

import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class Constituency {
	Integer number;
	String name;
	Integer electorate;
	Integer voter;
	private FederalState federalState;
	private LinkedList<Party> result;
	private LinkedList<MapMarkerPolygon> polygons;
	
	public Constituency(Integer number, String name, Integer electorate, Integer voter, FederalState federalState, LinkedList<Party> result, LinkedList<MapMarkerPolygon> polygons) {
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

	public LinkedList<MapMarkerPolygon> getPolygons() {
		return polygons;
	}

	public String getName() {
		return name;
	}

}
