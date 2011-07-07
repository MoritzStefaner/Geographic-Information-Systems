package org.gis.data.election;

import java.sql.Time;
import java.text.DateFormat;
import org.gis.data.GisPoint;
import org.postgis.Point;

/**
 * This represents a point of the mobile phone data of Malte Spitz, a German
 * politican of the green party.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class MaltePoint extends GisPoint {
	private static final long serialVersionUID = -3135502834709755141L;
	private Integer id;
	private Time starttime;
	private Time endtime;
	private String service;
	private String inoutgoing;
	private Integer direction;
	private String cella;
	private String cellb;

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
	
	/**
	 * Returns data information belonging to this point as text.
	 * 
	 * @return String with textual information.
	 */
	public String getInformation() {
		DateFormat df;
		df = DateFormat.getTimeInstance(DateFormat.SHORT);

		return "Start: " + df.format(getStarttime()) + "\n" +
			   "End: " + df.format(getEndtime()) + "\n" +
			   "Service: " + getService() + "\n" + 
			   "In/Outgoing: " + getInoutgoing() + "\n" +
			   "Cell A: " + getCella() + "\n" +
			   "Cell B: " + getCellb() + "\n";
	}
}
