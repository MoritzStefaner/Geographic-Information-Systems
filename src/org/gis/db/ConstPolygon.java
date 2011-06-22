package org.gis.db;

import org.postgis.*;

public class ConstPolygon extends Polygon {
	private Constituency constituency;
	
	public ConstPolygon(Constituency c, LinearRing[] rings){
		super(rings);
		
		constituency = c;
	}
	
	public void setConstituency(Constituency c) {
		constituency = c;
	}

	@Override
	public String getText() {
		if (constituency == null)
			return "No text available";
		else
			return constituency.getName();
	}

}
