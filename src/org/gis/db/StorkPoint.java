package org.gis.db;

import org.postgis.*;

public class StorkPoint extends Point {
	
	private String timestamp;
	private int locIdentifier;
	
	public StorkPoint(String stamp, int id, Point point) {
		this.timestamp = stamp;
		this.locIdentifier = id;
		setPoint(point);
	}
	
	protected void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public int getIdentifier(){
		return this.locIdentifier;
	}
	
	public String getTimestamp(){
		return this.timestamp;
	}
}
