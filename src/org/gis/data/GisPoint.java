package org.gis.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.gis.data.world.CountryPolygon;
import org.gis.tools.Database;
import org.postgis.Point;

/**
 * Represents a simple point and provides the possibility to
 * do same topological comparisons.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class GisPoint extends Point {
	private static final long serialVersionUID = -1601394576802344349L;

	// The enumeration for the relation between a point and a polygon.
	public enum PointRelation{
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
	public Double compareToPoint(GisPoint point){
		double meter = 0;
		Database db = Database.getDatabase();
		
		try {
			ResultSet result = db.executeQuery("SELECT Distance_Sphere(GeomFromText('" + this + "', 4326), GeomFromText('" + point + "', 4326));");
				result.next();
				meter = (Double) result.getObject(1);
				result.close();
			
		} catch (Exception e) {
			System.out.println(this);
			System.out.println(point);
			e.printStackTrace();
			System.exit(1);
		}
		
		return meter/1000;
	}
	
	/**
	 * Hard task b.
	 * 
	 * Describes the relation of this point to a polygon.
	 * 
	 * @param polygon
	 * @return The enumeration value of the relation.
	 */
	private PointRelation compareToPolygon(Polygon polygon){
		boolean value = false;
		Database db = Database.getDatabase();
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
			return PointRelation.INSIDE;
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
				return PointRelation.BORDER;
			}	
		}
		
		return PointRelation.OUTSIDE;
	}
	
	/**
	 * Relates this point to the constituencies of Germany.
	 * 
	 * @return The position of this point as constituency id.
	 */
	public Integer compareToConstituencies(){
		Database db = Database.getDatabase();
		
		ResultSet result = db.executeQuery("SELECT wkr_nr FROM constituencies WHERE Contains(poly_geom, GeomFromText('" + this + "'))");
		
		try {
			if (result.next())
				return (Integer) result.getObject(1);
			else
				return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * Relates this point to a country.
	 * 
	 * @return The relation to this point as enumeration.
	 */
	
	public PointRelation compareToCountry(LinkedList<CountryPolygon> polygons){
				
		for(CountryPolygon polygon : polygons){
			PointRelation relation = compareToPolygon(polygon);
			
			if(relation == PointRelation.INSIDE || relation == PointRelation.BORDER){
				return relation;
			}
		}
		
		return PointRelation.OUTSIDE;
	}
}
