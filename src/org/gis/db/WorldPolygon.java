package org.gis.db;

import org.postgis.Point;

public class WorldPolygon extends Polygon {

	String fips;
	String iso2;
	String iso3;
	String name;
	
	int un;
	long area;
	long pop2005;
	int region;
	int subregion;
	
	public WorldPolygon() {
		super();
	}
	
	public WorldPolygon(String fips, String iso2, String iso3, int un, String name, long area, long pop2005, int region, int subregion, Point[] polygon) {
		super(polygon);
		
		this.fips = fips;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.name = name;
		this.un = un;
		this.area = area;
		this.pop2005 = pop2005;
		this.region = region;
		this.subregion = subregion;
	}
	
	protected String getFips(){
		return this.fips;
	}

	protected String getIso2() {
		return iso2;
	}

	protected String getIso3() {
		return iso3;
	}

	protected String getName() {
		return name;
	}

	protected int getUn() {
		return un;
	}

	protected long getArea() {
		return area;
	}

	protected long getPop2005() {
		return pop2005;
	}

	protected int getRegion() {
		return region;
	}

	protected int getSubregion() {
		return subregion;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return "Polygon";
	}
	
	
	
	

}
