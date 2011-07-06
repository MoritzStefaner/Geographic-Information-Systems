package org.gis.data.world;

import java.sql.Time;

import org.gis.data.GisPoint;
import org.postgis.Point;

public class StorkPoint extends GisPoint {
	private static final long serialVersionUID = -3905001789779713514L;
	private Integer id;
	private Time timestamp;
	private Integer localIdentifier;
	private Integer worldId;
	
	public StorkPoint(Integer id, Time stamp, Integer localIdentifier, Point point, Integer worldId) {
		super(point);
		this.id = id;
		this.timestamp = stamp;
		this.localIdentifier = localIdentifier;
		this.worldId = worldId;
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
