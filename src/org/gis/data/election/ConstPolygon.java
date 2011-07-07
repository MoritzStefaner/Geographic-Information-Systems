package org.gis.data.election;

import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;
import org.postgis.*;

/**
 * Each constituency can have several territories. This class models
 * such a territory, which is in fact a polygon.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class ConstPolygon extends MapMarkerPolygon {
	private static final long serialVersionUID = 7360713891145160594L;
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
