package org.gis.db;

import java.sql.Time;

import org.postgis.*;

public class StorkPoint extends Point {
	
	private Time timestamp;
	private int locIdentifier;
	
	public StorkPoint(Time stamp, int id, Point point) {
		this.timestamp = stamp;
		this.locIdentifier = id;
		setPoint(point);
	}
	
	protected void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	protected int getIdentifier(){
		return this.locIdentifier;
	}
	
	protected Time getTimestamp(){
		return this.timestamp;
	}
}
