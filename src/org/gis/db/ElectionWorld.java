package org.gis.db;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class ElectionWorld {
	private HashMap<Integer, Constituency> constituencyMap;
	private LinkedList<MapMarkerPolygon> drawList;
	private float maxGreenParty;
	private float minGreenParty;
	
	public ElectionWorld() {
		this.constituencyMap = ExportObjectsTool.exportElection2009();
		this.drawList = getElectionPolygons(this.constituencyMap);
		
		setGreenPartyExtrema();
	}
	
    private LinkedList<MapMarkerPolygon> getElectionPolygons(HashMap<Integer, Constituency> constituencies) {
    	LinkedList<MapMarkerPolygon> drawList = new LinkedList<MapMarkerPolygon>();
    	Iterator<Constituency> it = constituencies.values().iterator();
    	Constituency constituency;
    	
    	while (it.hasNext()) {
    		constituency = it.next();
    		drawList.addAll(constituency.getPolygons());
    	}
    	
    	return drawList;
    }

	public HashMap<Integer, Constituency> getConstituencyMap() {
		return constituencyMap;
	}

	public LinkedList<MapMarkerPolygon> getDrawList() {
		return drawList;
	}
    
	/**
	 * Relates a point to the countries in the world.
	 * 
	 * @return The position of the point as country id.
	 */
	public Integer compareToGermany(GisPoint point){
		Database db = new Database();
		
		ResultSet result = db.executeQuery("SELECT wkr_nr FROM constituencies WHERE Contains(poly_geom, GeomFromText('"+point+"'))");
		
		try {
			if (result.next())
				return (Integer) result.getObject(1);
			else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	private void setGreenPartyExtrema() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		maxGreenParty = 0;
		minGreenParty = 1;
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			boolean found = false;
			while (it2.hasNext() && !found) {
				Party p = it2.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					float percentage = p.getZweitstimmen() / (float)c.getVoter();
					if (percentage > maxGreenParty) 
						maxGreenParty = percentage;
					if (percentage < minGreenParty)
						minGreenParty = percentage;
					
					found = true;
				}
			}
		}
	}
	
	public void setColorByGreenPartyLinear() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			boolean found = false;
			while (it2.hasNext() && !found) {
				Party p = it2.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					Iterator<MapMarkerPolygon> it3 = c.getPolygons().iterator();
					float alpha = (p.getZweitstimmen() / (float)c.getVoter() - minGreenParty) / (maxGreenParty - minGreenParty);
					
					while (it3.hasNext()) {
						MapMarkerPolygon m = it3.next();
						m.setColor(new Color((float) ((1 - alpha)*0.6 + 0.4), 1.0f, 1 - alpha, 0.8f));
					}
					
					found = true;
				}
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
}
