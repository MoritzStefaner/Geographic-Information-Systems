package org.gis.data.election;

import java.sql.Time;

import org.gis.data.GisPoint;
import org.postgis.Point;

public class MaltePoint extends GisPoint {
	
	private Integer id;
	private Time starttime;
	private Time endtime;
	private String service;
	private String inoutgoing;
	private Integer direction;
	private String cella;
	private String cellb;
	private Integer constId;

	public MaltePoint(Integer id, Time starttime, Time endtime, String service, String inoutgoing, Integer direction, String cella, String cellb, Integer constId, Point point) {
		super(point);
		
		this.id = id;
		this.starttime = starttime;
		this.endtime = endtime;
		this.service = service;
		this.inoutgoing = inoutgoing;
		this.direction = direction;
		this.cella = cella;
		this.cellb = cellb;
		this.constId = constId;
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
