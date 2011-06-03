package org.gis.db;

import java.util.*;

public class Constituency {

	int number;
	FederalState federalState;
	LinkedList<Party> wahlergebnis;
	LinkedList<ConstPolygon> polygons;
	
	public Constituency(int number, FederalState federalState, LinkedList<Party> wahlergebnis, LinkedList<ConstPolygon> polygons) {
		this.number = number;
		this.federalState = federalState;
		this.wahlergebnis = wahlergebnis;
		this.polygons = polygons;
	}

	protected int getNumber() {
		return number;
	}

	protected FederalState getFederalState() {
		return federalState;
	}

	protected LinkedList<Party> getWahlergebnis() {
		return wahlergebnis;
	}

	protected LinkedList<ConstPolygon> getPolygons() {
		return polygons;
	}

}
