package org.gis.db;

import java.util.HashMap;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class World {
	private HashMap<Integer, MapMarkerPolygon> countries;
	
	public World() {
		this.countries = ExportObjectsTool.exportWorld();
	}

	public HashMap<Integer, MapMarkerPolygon> getCountries() {
		return countries;
	}
    
	public LinkedList<MapMarkerPolygon> getWorldPolygons() {
		LinkedList<MapMarkerPolygon> list = new LinkedList<MapMarkerPolygon>(countries.values());
		return list;
	}
    
}
