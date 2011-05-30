package org.gis.db;

import java.sql.Time;

import org.postgis.*;

public class StorkPoint extends Point {
	
	private Time timestamp;
	
	public StorkPoint(Time stamp, Point point) {
		this.timestamp = stamp;
		setPoint(point);
	}
	
	protected void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	protected Time getTimestamp(){
		return this.timestamp;
	}
}
