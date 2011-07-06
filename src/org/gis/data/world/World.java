package org.gis.data.world;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.gis.data.GisPoint;
import org.gis.tools.Database;
import org.gis.tools.ExportObjectsTool;

public class World {
	private HashMap<Integer, Country> countries;
	private HashMap<Integer, StorkPoint> storks;
	private int largestCountrySize = 0;
	private StorkPoint lastPoint;
	private Country lastPolygon;
	
	public StorkPoint getLastPoint() {
		return lastPoint;
	}

	public void setLastPoint(StorkPoint lastPoint) {
		this.lastPoint = lastPoint;
	}

	public Country getLastPolygon() {
		return lastPolygon;
	}

	public void setLastPolygon(Country lastPolygon) {
		this.lastPolygon = lastPolygon;
	}
	
	public World() {
		this.countries = ExportObjectsTool.exportWorld();
	}
	
	public void loadAllStorks() {
		this.storks = ExportObjectsTool.exportStork();
	}
	
	public void loadSelectedStorks(int id) {
		this.storks = ExportObjectsTool.exportStorkSelected(id);
	}
	
	public void setStorks(HashMap<Integer, StorkPoint> s) {
		this.storks = s;
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
	
    public Country getCountry(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	Integer i = compareToWorld(p);
    	
    	if (i != null) {
    		Country c = getCountries().get(i);
    		return c;
    	}
    	return null;
    }
    
	public StorkPoint getStorkPoint(double longitude, double latitude) {
		GisPoint p = new GisPoint(latitude, longitude);
		Database db = Database.getDatabase();
		
		ResultSet result = db.executeQuery("SELECT id, DISTANCE(GeomFromText('" + p + "'), geometrycolumn) AS d FROM storks ORDER BY d LIMIT 1");
		
		try {
			if (result.next())
				return storks.get((Integer) result.getObject(1));
			else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
			if (result.next())
				return (Integer) result.getObject(1);
			
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Hard task d.
	 * 
	 * Searches for country by their size
	 * 
	 * @return The id of the country.
	 */
	public LinkedList<Integer> getLargest(){
		Database db = Database.getDatabase();
		
		// Ignores the Antartica as a country.
		ResultSet result = db.executeQuery("SELECT id, name, area FROM world WHERE name != 'Antarctica' ORDER BY area DESC");
		
		LinkedList<Integer> list = new LinkedList<Integer>();
		try {
			while (result.next()) {
				if (largestCountrySize == 0)
					largestCountrySize = (Integer) result.getObject(3);
				list.add((Integer) result.getObject(1));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setColorRandom() {
		Iterator<Country> it2 = getCountries().values().iterator();
		
		while (it2.hasNext()) {
			Country wp = it2.next();
			
			Random rand = new Random();
			wp.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.5f));
		}
	}
	
	public void setColorBySize() {
		Iterator<Integer> it = getLargest().iterator();
		
		
		while (it.hasNext()) {
			Country c = getCountries().get(it.next());
			float alpha = (float) Math.pow(c.getArea() / (float) largestCountrySize, 0.33);
			c.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
		}
	}
	
	public void setColorByTravelThrough() {
		setColorByTravelThrough(0, true);
	}
	
	public void setColorByTravelThrough(int id) {
		setColorByTravelThrough(id, false);
	}
	
	public void setColorByTravelThrough(int id, boolean all) {
		Iterator<Country> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			Country mmp = it.next();
			mmp.setColor(Color.WHITE);
		}
		
		Iterator<Integer> it2 = getStorkTravel(id, all).iterator();	
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
		Iterator<Country> it = getCountries().values().iterator();
		
		while (it.hasNext()) {
			Country mmp = it.next();
			mmp.setColor(Color.WHITE);
		}
		
		LinkedList<Country> list = new LinkedList<Country>();
    	Database db = Database.getDatabase();
    	
    	ResultSet result;
    	if (all)
    		result = db.executeQuery("SELECT world.id, COUNT(storks.id) FROM world, storks WHERE storks.world_id = world.id GROUP BY world.id");
    	else
    		result = db.executeQuery("SELECT world.id, COUNT(storks.id) FROM world, storks WHERE storks.taglocalidentifier = " + id + " AND storks.world_id = world.id GROUP BY world.id");
    		
    	int max = 0;
    	try {
			while(result.next()) {
				Country wp = getCountries().get((Integer) result.getObject(1));
				int now = ((Long) result.getObject(2)).intValue();
				wp.setAmountStorks(now);
				list.add(wp);
				if (now > max)
					max = now;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Iterator<Country> it2 = list.iterator();
		while (it2.hasNext()) {
			Country wp = it2.next();
			float alpha = (float) Math.pow((wp.getAmountStorks() / (float) max), 0.33);
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
    public LinkedList<Integer> getStorkTravel(int id, boolean all) {
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	Database db = Database.getDatabase();
    	
    	ResultSet result;
    	if (all)
    		result = db.executeQuery("SELECT DISTINCT world.id FROM world, storks WHERE storks.world_id = world.id");
    	else
    		result = db.executeQuery("SELECT DISTINCT world.id FROM world, storks WHERE storks.taglocalidentifier = " + id + " AND storks.world_id = world.id");
    	
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
    
    
    public Integer getStorkId(int selected) {
    	if (selected == 1)
			return 91397;
		else if (selected == 2)
			return 77195;
		else if (selected == 3)
			return 91398;
		else if (selected == 4)
			return 91398;
		else if (selected == 5)
			return 93412;
		else if (selected == 6)
			return 93411;
		else if (selected == 7)
			return 54977;
		else if (selected == 8)
			return 14544;
		else if (selected == 9)
			return 40534;
		else if (selected == 10)
			return 54983;
		else if (selected == 11)
			return 54988;
		else if (selected == 12)
			return 14543;
		else
			return null;
    }
}
