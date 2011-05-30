package org.gis.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.gis.db.Database;
import org.gis.db.StorkPoint;
import org.gis.db.WorldPolygon;
import org.postgis.*;

public class ExportObjectsTool {
	
	private Database db;

	public ExportObjectsTool(){
		this.db = new Database();
	}
	
	private LinkedList<StorkPoint> exportStork(){
		ResultSet result = db.executeQuery("select timestamp, altitude, taglocalidentifier, geometrycolumn from stork");
		LinkedList<StorkPoint> pointList = new LinkedList<StorkPoint>();
		
		try {
			while(result.next()){
				
				PGgeometry geom = (PGgeometry) result.getObject(4);
				Point point = (Point) geom.getGeometry();
				point.setZ((Integer) result.getObject(2));
				StorkPoint spoint = new StorkPoint((String) result.getObject(1), (Integer) result.getObject(3), point);
				
				pointList.add(spoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointList;
		
	}

	private LinkedList<WorldPolygon> exportWorld(){
		ResultSet result = db.executeQuery("select id, fips, iso2, iso3, un, name, area, pop2005, region, subregion, lon, lat, poly_geom from world");
		LinkedList<WorldPolygon> polygonList = new LinkedList<WorldPolygon>();
		
		try {
			while(result.next()){
				PGgeometry geom = (PGgeometry) result.getObject(13);
				org.postgis.Polygon ngeom = (org.postgis.Polygon) geom.getGeometry();
				WorldPolygon polygon = new WorldPolygon((Integer) result.getObject(1), (String) result.getObject(2), (String) result.getObject(3), 
						(String) result.getObject(4), (Integer) result.getObject(5), (String) result.getObject(6), (Long) result.getObject(7),
						(Long) result.getObject(8), (Integer) result.getObject(9), (Integer) result.getObject(10), (Double) result.getObject(11),
						(Double) result.getObject(12), ngeom.getRing(0).getPoints());

				polygonList.add(polygon);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return polygonList;
	}
	
	public static void main(String[] args) {
		
		ExportObjectsTool eot = new ExportObjectsTool();
		
		//eot.exportStork();
		eot.exportWorld();

	}

}
