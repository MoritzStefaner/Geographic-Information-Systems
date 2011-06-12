package org.gis.db;

import org.postgis.*;

public class WorldPolygon extends Polygon {

	String fips;
	String iso2;
	String iso3;
	String name;
	
	Integer un;
	Integer area;
	Integer pop2005;
	Integer region;
	Integer subregion;
	
	public WorldPolygon(String fips, String iso2, String iso3, Integer un, String name, Integer area, Integer pop2005, Integer region, Integer subregion, LinearRing[] rings) {
		super(rings);
		
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

	public String getName() {
		return name;
	}

	protected Integer getUn() {
		return un;
	}

	protected Integer getArea() {
		return area;
	}

	protected Integer getPop2005() {
		return pop2005;
	}

	protected Integer getRegion() {
		return region;
	}

	protected Integer getSubregion() {
		return subregion;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return getName();
	}
}
