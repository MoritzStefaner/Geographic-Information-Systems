package org.gis.db;

import java.sql.Time;

import org.postgis.Point;

public class StorkPoint extends GisPoint {
	
	private Integer id;
	private Time timestamp;
	private Integer localIdentifier;
	
	public StorkPoint(Integer id, Time stamp, Integer localIdentifier, Point point) {
		super(point);
		this.id = id;
		this.timestamp = stamp;
		this.localIdentifier = localIdentifier;
	}
	
	public Time getTimestamp(){
		return this.timestamp;
	}
	
	public Integer getId(){
		return this.id;
	}
	
	public Integer getLocalIdentifier(){
		return this.localIdentifier;
	}
}
