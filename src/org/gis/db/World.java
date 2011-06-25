package org.gis.db;

import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	/**
	 * Relates a point to the countries in the world.
	 * 
	 * @return The position of the point as country id.
	 */
	public Integer compareToWorld(GisPoint point){
		
		Database db = new Database();
		
		ResultSet result = db.executeQuery("SELECT id FROM world WHERE Contains(poly_geom, GeomFromText('"+point+"'))");
		
		try {
			result.next();
			return (Integer) result.getObject(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Hard task d.
	 * 
	 * Searches for the largest country by area.
	 * 
	 * @return The id of the country.
	 */
	public Integer getLargest(){
		Database db = new Database();
		
		ResultSet result = db.executeQuery("SELECT id, Area(GeomFromText(poly_geom, 4326))  FROM world ORDER BY area DESC");
		
		
		try {
			result.next();
			return (Integer) result.getObject(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Hard task e.
	 * 
	 * Calculates all countries through which the storks did travel.
	 * 
	 * @return A list of the countries.
	 */
    public LinkedList<MapMarkerPolygon> getStorkTravel(){
    	LinkedList<MapMarkerPolygon> list = new LinkedList<MapMarkerPolygon>();
    	Database db = new Database();
    	
    	// TODO Performance reduzieren.
    	ResultSet result = db.executeQuery("SELECT DISTINCT world.id FROM world, storks WHERE Contains(poly_geom, geometrycolumn)");
    	
    	try {
			while(result.next()){
				MapMarkerPolygon polygon = countries.get((Integer) result.getObject(1));
				list.add(polygon);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return list;
    }
}
