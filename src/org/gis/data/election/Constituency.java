package org.gis.data.election;

import java.awt.Color;
import java.util.*;

import org.gis.data.GisPoint;
import org.gis.data.GisPoint.PointRelation;
import org.gis.data.Polygon.PolygonRelation;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

/**
 * A constituency at the time of the election 2009.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
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

	/**
	 * Returns an information string about the constituency.
	 * 
	 * @return Textual Information about the constituency
	 */
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
	
	/**
	 * Sets the color for all polygons belonging to this constituency.
	 * 
	 * @param c new Color
	 */
	public void setColor(Color c) {
		Iterator<ConstPolygon> it = getPolygons().iterator();
		while (it.hasNext()) {
			MapMarkerPolygon m = it.next();
			m.setColor(c);
		}
	}
	
	/**
	 * Returns the relationship between the Point and the 
	 * Constituency (INSIDE, BORDER or OUTSIDE)
	 * 
	 * @param p Point to compare to
	 * @return Relationship between the Point and the Constituency (INSIDE, BORDER or OUTSIDE)
	 */
	public PointRelation compareTo(GisPoint p) {
		Iterator<ConstPolygon> it = getPolygons().iterator();
		PointRelation r = PointRelation.OUTSIDE;
		
		while (r == PointRelation.OUTSIDE && it.hasNext()) {
			ConstPolygon cp = it.next();
			r = cp.compareToPoint(p);
		}
		
		return r;
	}
	
	/**
	 * Returns the relationship between the Point and the 
	 * Constituency regarding to the 9-cut model
	 * 
	 * @param c Constituency to compare to
	 * @return PolygonRelationship between the Point and the Constituency
	 */
	public PolygonRelation compareTo(Constituency c) {
		Iterator<ConstPolygon> it = c.getPolygons().iterator();
		Iterator<ConstPolygon> it2 = getPolygons().iterator();
		PolygonRelation r = PolygonRelation.DISJOINT;
		
		while (it.hasNext()) {
			ConstPolygon cp = it.next();
			while (it2.hasNext()) {
				ConstPolygon cp2 = it2.next();
				r = cp.compareToPolygon(cp2);
			}
		}
		
		return r;
	}
}
