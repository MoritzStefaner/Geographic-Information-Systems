package org.gis.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.Point;

public class GisPoint extends Point {
	
	public enum Relation{
		INSIDE, OUTSIDE, BORDER
	}
	
	public GisPoint(Point point){
		setPoint(point);
	}
	
	public void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public Double compareTo(GisPoint point){
		double kilometer = 0;
		double meter = 0;
		Database db = new Database();
		
		ResultSet result = db.executeQuery("SELECT ST_Distance(gg1, gg2) FROM (SELECT	ST_GeographyFromText('SRID=4326;"+this+"') As gg1, " +
				"ST_GeographyFromText('SRID=4326;"+point+"') As gg2) As foo;");
		
		try {
			result.next();
			meter = (Double) result.getObject(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		kilometer = meter/1000;
		return kilometer;
	}
	
	public Relation compareTo(Polygon polygon){
		boolean value = false;
		Database db = new Database();
		
		ResultSet contains = db.executeQuery("SELECT ST_Contains(polygon, point) FROM (SELECT ST_Buffer(ST_GeomFromText('"+polygon+"'),0) As polygon, " +
				"ST_Buffer(ST_GeomFromText('"+this+"'),0) As point) As foo;");
		try {
			contains.next();
			value = (Boolean) contains.getObject(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(value){
			return Relation.INSIDE;
		}else{
			ResultSet touches = db.executeQuery("SELECT ST_Touches('"+polygon+"'::geometry, '"+this+"'::geometry);");
			
			try {
				touches.next();
				value = (Boolean) touches.getObject(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(value){
				return Relation.BORDER;
			}	
		}
		
		return Relation.OUTSIDE;
	}
	
}
