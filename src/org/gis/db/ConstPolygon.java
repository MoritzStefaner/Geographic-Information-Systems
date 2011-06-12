package org.gis.db;

import org.postgis.*;

public class ConstPolygon extends Polygon{
	

	public ConstPolygon(LinearRing[] rings){
		super(rings);
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
