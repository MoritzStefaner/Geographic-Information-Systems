package org.gis.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.Point;

public class GisPoint extends Point{
	
	public void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public double compareTo(GisPoint point){
		double kilometer = 0;
		Database db = new Database();
		
		String query = "SELECT ST_Distance(gg1, gg2) As distance FROM (SELECT	ST_GeographyFromText('SRID=4326;POINT("+this.x+" "+this.y+")') As gg1, " +
				"ST_GeographyFromText('SRID=4326;POINT("+point.x+" "+point.y+")') As gg2) As foo;";
		ResultSet result = db.executeQuery(query);
		
		
		try {
			result.next();
			kilometer = (Double) result.getObject(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return kilometer;
	}
}
