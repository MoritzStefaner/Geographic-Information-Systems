package org.gis.db;

import org.postgis.Point;

public class WorldPolygon extends Polygon {

	String fips;
	String iso2;
	String iso3;
	String name;
	
	int id;
	int un;
	long area;
	long pop2005;
	int region;
	int subregion;
	
	double lon;
	double lat;
	
	
	public WorldPolygon(int id , String fips, String iso2, String iso3, int un, String name, long area, long pop2005, int region, int subregion, double lon, double lat, Point[] polygon) {
		super(polygon);
		
		this.id = id;
		this.fips = fips;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.name = name;
		this.un = un;
		this.area = area;
		this.pop2005 = pop2005;
		this.region = region;
		this.subregion = subregion;
		this.lon = lon;
		this.lat = lat;
	}
	
	

}
