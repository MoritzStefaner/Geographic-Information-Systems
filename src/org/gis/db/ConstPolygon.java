package org.gis.db;

import org.postgis.Point;

public class ConstPolygon extends Polygon{
	private Constituency constituency;

	public ConstPolygon(Point[] polygon, Constituency constituency){
		super(polygon);
		
		this.constituency = constituency;
	}

	@Override
	public String getText() {
		return constituency.getName();
	}

}
