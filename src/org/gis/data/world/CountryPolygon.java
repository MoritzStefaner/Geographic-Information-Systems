package org.gis.data.world;

import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;
import org.postgis.*;

public class CountryPolygon extends MapMarkerPolygon {
	
	private Country country;
	
	public CountryPolygon(Country country, LinearRing[] rings) {
		super(rings);
		
		this.country = country;
	}
	

	@Override
	public String getText() {
		if (country == null){
			return "No text available";
		}else{ 
			return country.getName();
		}
	}
}
