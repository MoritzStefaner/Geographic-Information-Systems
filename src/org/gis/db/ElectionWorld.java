package org.gis.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class ElectionWorld {
	private HashMap<Integer, Constituency> constituencyMap;
	private LinkedList<MapMarkerPolygon> drawList;
	
	public ElectionWorld() {
		this.constituencyMap = ExportObjectsTool.exportElection2009();
		this.drawList = getElectionPolygons(this.constituencyMap);
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
    
    
}
