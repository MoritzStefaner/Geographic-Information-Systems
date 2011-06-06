package org.gis.db;

import java.sql.Time;

public class StorkPoint extends GisPoint {
	
	private int id;
	private Time timestamp;
	private int localIdentifier;
	
	public StorkPoint(int id, Time stamp, int localIdentifier, GisPoint point) {
		this.id = id;
		this.timestamp = stamp;
		this.localIdentifier = localIdentifier;
		setPoint(point);
	}
	
	public Time getTimestamp(){
		return this.timestamp;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getLocalIdentifier(){
		return this.localIdentifier;
	}
}
