package org.gis.data.election;

import java.awt.Color;
import java.util.*;

import org.gis.data.GisPoint;
import org.gis.data.GisPoint.Relation;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class Constituency {
	Integer number;
	String name;
	Integer electorate;
	Integer voter;
	private FederalState federalState;
	private HashMap<String, PartyResults> result;
	private LinkedList<ConstPolygon> polygons;
	private int malteOccurence;
	
	public Constituency(Integer number, String name, Integer electorate, Integer voter, FederalState federalState, HashMap<String, PartyResults> result) {
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

	public HashMap<String, PartyResults> getResult() {
		return result;
	}

	public Integer getNumber() {
		return number;
	}

	public FederalState getFederalState() {
		return federalState;
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
		
		Iterator<PartyResults> it = getResult().values().iterator();
		while (it.hasNext()) {
			PartyResults p = it.next();
			s = s + p.getName() + ": " + p.getZweitstimmen() + "\n";
		}
		
		return s;
	}
	
	public void setColor(Color c) {
		Iterator<ConstPolygon> it = getPolygons().iterator();
		while (it.hasNext()) {
			MapMarkerPolygon m = it.next();
			m.setColor(c);
		}
	}
	
	public Relation compareTo(GisPoint p) {
		Iterator<ConstPolygon> it = getPolygons().iterator();
		Relation r = Relation.OUTSIDE;
		
		while (r == Relation.OUTSIDE && it.hasNext()) {
			ConstPolygon cp = it.next();
			r = cp.compareTo(p);
		}
		
		return r;
	}
	
	//TODO
	public String compareTo(Constituency c) {
		Iterator<ConstPolygon> it = c.getPolygons().iterator();
		Iterator<ConstPolygon> it2 = getPolygons().iterator();
		String r = null;
		
		while (it.hasNext()) {
			ConstPolygon cp = it.next();
			while (it2.hasNext()) {
				ConstPolygon cp2 = it2.next();
				r = cp.compareTo(cp2);
			}
		}
		
		return r;
	}
}
