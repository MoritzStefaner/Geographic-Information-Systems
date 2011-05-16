package org.gis.db;

import org.postgis.*;

public class StorkPoint extends Point {
	private static final long serialVersionUID = -2712139067711126319L;
	
	private String timestamp;
	private int locIdentifier;
	
	public StorkPoint() {
		
	}
	
	public void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
	
	public void setTime(String stamp){
		this.timestamp = stamp;
	}
	
	public void setIdentifier(int id){
		this.locIdentifier = id;
	}
	
	public int getIdentifier(){
		return this.locIdentifier;
	}
	
	public String getTimestamp(){
		return this.timestamp;
	}
}
