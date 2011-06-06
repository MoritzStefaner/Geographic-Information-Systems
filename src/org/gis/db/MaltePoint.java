package org.gis.db;

import java.sql.Time;

public class MaltePoint extends GisPoint{
	
	private int id;
	private Time starttime;
	private Time endtime;
	private String service;
	private String inoutgoing;
	private int direction;
	private String cella;
	private String cellb;

	public MaltePoint(int id, Time starttime, Time endtime, String service, String inoutgoing, int direction, String cella, String cellb, GisPoint point) {
		this.id = id;
		this.starttime = starttime;
		this.endtime = endtime;
		this.service = service;
		this.inoutgoing = inoutgoing;
		this.direction = direction;
		this.cella = cella;
		this.cellb = cellb;
		setPoint(point);
	}

	public Time getStarttime() {
		return starttime;
	}

	public Time getEndtime() {
		return endtime;
	}

	public String getService() {
		return service;
	}

	public String getInoutgoing() {
		return inoutgoing;
	}

	public String getCella() {
		return cella;
	}

	public String getCellb() {
		return cellb;
	}


	public int getDirection() {
		return direction;
	}
	
	public int getId(){
		return id;
	}
}
