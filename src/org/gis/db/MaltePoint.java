package org.gis.db;

import java.sql.Time;

import org.postgis.Point;

public class MaltePoint extends Point{
	
	Time starttime;
	Time endtime;
	String service;
	String inoutgoing;
	int direction;
	String cella;
	String cellb;

	public MaltePoint(Time starttime, Time endtime, String service, String inoutgoing, int direction, String cella, String cellb, Point point) {
		this.starttime = starttime;
		this.endtime = endtime;
		this.service = service;
		this.inoutgoing = inoutgoing;
		this.direction = direction;
		this.cella = cella;
		this.cellb = cellb;
		setPoint(point);
	}
	
	
	protected void setPoint(Point point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}

	protected Time getStarttime() {
		return starttime;
	}

	protected Time getEndtime() {
		return endtime;
	}

	protected String getService() {
		return service;
	}

	protected String getInoutgoing() {
		return inoutgoing;
	}

	protected String getCella() {
		return cella;
	}

	protected String getCellb() {
		return cellb;
	}


	protected int getDirection() {
		return direction;
	}
	
	
}
