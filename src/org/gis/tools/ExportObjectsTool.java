package org.gis.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.gis.db.Database;
import org.gis.db.Polygon;
import org.gis.db.StorkPoint;
import org.postgis.PGgeometry;
import org.postgis.Point;

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
				StorkPoint spoint = new StorkPoint();
				spoint.setTime((String) result.getObject(1));
				spoint.setIdentifier((Integer) result.getObject(3));
				PGgeometry geom = (PGgeometry) result.getObject(4);
				Point point = (Point) geom.getGeometry();
				point.setZ((Integer) result.getObject(2));
				spoint.setPoint(point);
				
				pointList.add(spoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pointList;
		
	}

	private LinkedList<Polygon> exportWorld(){
		ResultSet result = db.executeQuery("select poly_geom from world");
		LinkedList<Polygon> polygonList = new LinkedList<Polygon>();
		
		try {
			while(result.next()){
				PGgeometry geom = (PGgeometry) result.getObject(1);
				org.postgis.Polygon ngeom = (org.postgis.Polygon) geom.getGeometry();
				Polygon polygon = new Polygon();
				polygon.setRing(ngeom.getRing(0).getPoints());
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
