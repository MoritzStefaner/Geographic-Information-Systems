package org.gis.db;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;

public class World {
	private HashMap<Integer, Country> countries;
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

	public HashMap<Integer, Country> getCountries() {
		return countries;
	}
    
	public LinkedList<Country> getCountryPolygons() {
		LinkedList<Country> list = new LinkedList<Country>(countries.values());
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
		
		// Ignores the Antartica as a country.
		ResultSet result = db.executeQuery("SELECT id, name, Area(GeomFromText(poly_geom, 4326))  FROM world WHERE name != 'Antarctica' ORDER BY area DESC");
		
		try {
			result.next();
			return (Integer) result.getObject(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setColorByTravelThrough() {
		Iterator<Country> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			Country mmp = it.next();
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
		Iterator<CountryPolygon> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			CountryPolygon mmp = it.next();
			mmp.setColor(Color.WHITE);
		}
		
		LinkedList<CountryPolygon> list = new LinkedList<CountryPolygon>();
    	Database db = Database.getDatabase();
    	
    	ResultSet result = db.executeQuery("SELECT world.id, COUNT(storks.id) FROM world, storks WHERE storks.world_id = world.id GROUP BY world.id");
    	
    	int max = 0;
    	try {
			while(result.next()) {
				CountryPolygon wp = getCountries().get((Integer) result.getObject(1));
				int now = ((Long) result.getObject(2)).intValue();
				wp.setAmountStorks(now);
				list.add(wp);
				if (now > max)
					max = now;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Max: " + max);
		Iterator<CountryPolygon> it2 = list.iterator();
		while (it2.hasNext()) {
			CountryPolygon wp = it2.next();
			float alpha = (float) Math.pow((wp.getAmountStorks() / (float) max), 1 / (float)3);
			System.out.println(wp.getAmountStorks());
			wp.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
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
