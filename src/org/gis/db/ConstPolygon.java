package org.gis.db;

import org.postgis.Point;

public class ConstPolygon extends Polygon{
	

	public ConstPolygon(Point[] polygon){
		super(polygon);
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
