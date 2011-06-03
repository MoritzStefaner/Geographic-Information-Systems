package org.gis.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

import org.gis.db.Constituency;
import org.gis.db.Database;
import org.gis.db.MaltePoint;
import org.gis.db.StorkPoint;
import org.gis.db.WorldPolygon;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;
import org.postgis.*;

public class ExportObjectsTool {
	
	private Database db;

	public ExportObjectsTool(){
		this.db = new Database();
	}
	
	public HashMap<Integer, StorkPoint> exportStork(){
		ResultSet result = db.executeQuery("select timestamp, altitude, taglocalidentifier, geometrycolumn from storks");
		HashMap<Integer, StorkPoint> pointMap = new HashMap<Integer, StorkPoint>();
		
		try {
			while(result.next()){
				
				PGgeometry geom = (PGgeometry) result.getObject(4);
				Point point = (Point) geom.getGeometry();
				point.setZ((Integer) result.getObject(2));
				StorkPoint spoint = new StorkPoint((Time) result.getObject(1), point);
				pointMap.put((Integer) result.getObject(3), spoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointMap;	
	}
	
	public HashMap<Integer, MaltePoint> exportMalte(){
		ResultSet result = db.executeQuery("select id, starttime, endtime, service, inoutgoing, direction, cella, cellb, geometrycolumn from malte");
		HashMap<Integer, MaltePoint> pointMap = new HashMap<Integer, MaltePoint>();
		
		try {
			while(result.next()){
				PGgeometry geom = (PGgeometry) result.getObject(9);
				Point point = (Point) geom.getGeometry();
				MaltePoint mpoint = new MaltePoint((Time) result.getObject(2), (Time) result.getObject(3), 
						(String) result.getObject(4), (String) result.getObject(5), (Integer) result.getObject(6), (String) result.getObject(7), (String) result.getObject(8), point);
				pointMap.put((Integer) result.getObject(1), mpoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointMap;	
	}

	public HashMap<Integer, WorldPolygon> exportWorld(){
		ResultSet result = db.executeQuery("select id, fips, iso2, iso3, un, name, area, pop2005, region, subregion, poly_geom from world");
		HashMap<Integer, WorldPolygon> polygonMap = new HashMap<Integer, WorldPolygon>();
		
		try {
			while(result.next()){
				PGgeometry geom = (PGgeometry) result.getObject(11);
				org.postgis.Polygon ngeom = (org.postgis.Polygon) geom.getGeometry();
				WorldPolygon polygon = new WorldPolygon((String) result.getObject(2), (String) result.getObject(3), 
						(String) result.getObject(4), (Integer) result.getObject(5), (String) result.getObject(6), (Long) result.getObject(7),
						(Long) result.getObject(8), (Integer) result.getObject(9), (Integer) result.getObject(10), ngeom.getRing(0).getPoints());

				polygonMap.put((Integer) result.getObject(1), polygon);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return polygonMap;
	}
	
	public HashMap<Integer, Constituency> exportElection2009(){
		for(int i = 1; i < 300; i++){
			ResultSet result = db.executeQuery("select poly_geom from constituencies where wkr_nr ="+i);
			try {
				result.getObject(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
}
