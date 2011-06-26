package org.gis.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.Point;

public class GisPoint extends Point {
	
	// The enumeration for the relation between a point and a polygon.
	public enum Relation{
		INSIDE, OUTSIDE, BORDER
	}
	
	public GisPoint(Point point){
		setPoint(point);
	}
	
	public GisPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public GisPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	/**
	 * Hard task a.
	 * 
	 * Compares this point to another one and gives the distance.
	 * 
	 * @param point
	 * @return The distance in kilometers.
	 */
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
	
	/**
	 * Hard task b.
	 * 
	 * Describes the relation of this point to a polygon.
	 * 
	 * @param polygon
	 * @return The enumeration value of the relation.
	 */
	public Relation compareTo(Polygon polygon){
		boolean value = false;
		Database db = new Database();
		
		ResultSet contains = db.executeQuery("SELECT Contains(GeomFromText('"+polygon+"'), GeomFromText('"+this+"')) AS contains");
		try {
			contains.next();
			value = (Boolean) contains.getObject(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If this point isn't inside the polygon test if the point touches it.
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
	
	/**
	 * Relates this point to the constituencies of Germany.
	 * 
	 * @return The position of this point as constituency id.
	 */
	public Integer compareToConstituencies(){
		
		Database db = new Database();
		
		ResultSet result = db.executeQuery("SELECT wkr_nr FROM constituencies WHERE Contains(poly_geom, GeomFromText('"+this+"'))");
		
		try {
			result.next();
			return (Integer) result.getObject(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
