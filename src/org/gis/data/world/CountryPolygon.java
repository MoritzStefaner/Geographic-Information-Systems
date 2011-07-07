package org.gis.data.world;

import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;
import org.postgis.*;

/**
 * A country can consist of several territories, i.e. polygons. This
 * class models these different parts of a country.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class CountryPolygon extends MapMarkerPolygon {
	private static final long serialVersionUID = 7874854162340938836L;
	private Country country;
	
	public CountryPolygon(Country country, LinearRing[] rings) {
		super(rings);
		
		this.country = country;
	}
	
	public Country getCountry(){
		
		return this.country;
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
