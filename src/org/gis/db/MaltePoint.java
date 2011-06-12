package org.gis.db;

import java.sql.Time;

import org.postgis.Point;

public class MaltePoint extends GisPoint{
	
	private Integer id;
	private Time starttime;
	private Time endtime;
	private String service;
	private String inoutgoing;
	private Integer direction;
	private String cella;
	private String cellb;

	public MaltePoint(Integer id, Time starttime, Time endtime, String service, String inoutgoing, Integer direction, String cella, String cellb, Point point) {
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


	public Integer getDirection() {
		return direction;
	}
	
	public Integer getId(){
		return id;
	}
}
