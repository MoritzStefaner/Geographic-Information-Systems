package org.gis.db;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class World {
	private HashMap<Integer, MapMarkerPolygon> countries;
	private HashMap<Integer, StorkPoint> storks;
	
	public World() {
		this.countries = ExportObjectsTool.exportWorld();
	}
	
	public void loadAllStorks() {
		this.storks = ExportObjectsTool.exportStork();
	}
	
	public void loadSelectedStorks(int id) {
		this.storks = ExportObjectsTool.exportStorkSelected(id);
	}
	
	public HashMap<Integer, StorkPoint> getStorks() {
		return storks;
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
		Database db = Database.getDatabase();
		
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
		Database db = Database.getDatabase();
		
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
	
	public void setColorByTravelThrough() {
		Iterator<MapMarkerPolygon> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			MapMarkerPolygon mmp = it.next();
			mmp.setColor(Color.WHITE);
		}
		
		Iterator<Integer> it2 = getStorkTravel().iterator();	
		while (it2.hasNext()) {
			Integer i = it2.next();
			getCountries().get(i).setColor(Color.RED);
		}
	}
	
	public void setColorByTravelThroughPercentage() {
		setColorByTravelThroughPercentage(0, true);
	}

	public void setColorByTravelThroughPercentage(int id) {
		setColorByTravelThroughPercentage(id, false);
	}
	
	private void setColorByTravelThroughPercentage(int id, boolean all) {
		Iterator<MapMarkerPolygon> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			MapMarkerPolygon mmp = it.next();
			mmp.setColor(Color.WHITE);
		}
		
		LinkedList<MapMarkerPolygon> list = new LinkedList<MapMarkerPolygon>();
    	Database db = Database.getDatabase();
    	
    	ResultSet result = db.executeQuery("SELECT world.id, COUNT(storks.id) FROM world, storks WHERE storks.world_id = world.id GROUP BY world.id");
    	
    	int sum = 0;
    	try {
			while(result.next()) {
				list.add(getCountries().get((Integer) result.getObject(1)));
				sum += (Integer) result.getObject(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Iterator<Integer> it2 = getStorkTravel().iterator();	
		while (it2.hasNext()) {
			Integer i = it2.next();
			getCountries().get(i).setColor(Color.RED);
		}
	}
	
	/**
	 * Hard task e.
	 * 
	 * Calculates all countries through which the storks did travel.
	 * 
	 * @return An ID list of the countries.
	 */
    public LinkedList<Integer> getStorkTravel(){
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	Database db = Database.getDatabase();
    	
    	// TODO Performance reduzieren.
    	ResultSet result = db.executeQuery("SELECT DISTINCT world.id FROM world, storks WHERE storks.world_id = world.id");
    	
    	try {
			while(result.next()){
				list.add((Integer) result.getObject(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return list;
    }
}
