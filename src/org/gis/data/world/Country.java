package org.gis.data.world;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.data.GisPoint;
import org.gis.data.GisPoint.Relation;

public class Country {
	private String fips;
	private String iso2;
	private String iso3;
	private String name;
	
	private Integer un;
	private Integer area;
	private Integer pop2005;
	private Integer region;
	private Integer subregion;
	private LinkedList<CountryPolygon> polygons;
	
	private int amountStorks;
	
	public Country(String fips, String iso2, String iso3, Integer un, String name, Integer area, Integer pop2005, Integer region, Integer subregion){
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
	
	public String getFips(){
		return this.fips;
	}

	public LinkedList<CountryPolygon> getPolygons() {
		return polygons;
	}

	public String getIso2() {
		return iso2;
	}

	public String getIso3() {
		return iso3;
	}

	public String getName() {
		return name;
	}

	public int getAmountStorks() {
		return amountStorks;
	}
	
	public void setAmountStorks(int a) {
		amountStorks = a;
	}
	
	public Integer getUn() {
		return un;
	}

	public Integer getArea() {
		return area;
	}

	public Integer getPop2005() {
		return pop2005;
	}

	public Integer getRegion() {
		return region;
	}

	public Integer getSubregion() {
		return subregion;
	}
	
	public void addPolygons(LinkedList<CountryPolygon> list){
		this.polygons = list;
	}
	
	public void setColor(Color c) {
		Iterator<CountryPolygon> it = getPolygons().iterator();
		
		while (it.hasNext()) {
			CountryPolygon cp = it.next();
			cp.setColor(c);
		}
	}
	
	public Relation compareTo(GisPoint p) {
		Iterator<CountryPolygon> it = getPolygons().iterator();
		Relation r = Relation.OUTSIDE;
		
		while (r == Relation.OUTSIDE && it.hasNext()) {
			CountryPolygon cp = it.next();
			r = cp.compareTo(p);
		}
		
		return r;
	}
	
	//TODO
	public String compareTo(Country c) {
		Iterator<CountryPolygon> it = c.getPolygons().iterator();
		Iterator<CountryPolygon> it2 = getPolygons().iterator();
		String r = null;
		
		while (it.hasNext()) {
			CountryPolygon cp = it.next();
			while (it2.hasNext()) {
				CountryPolygon cp2 = it2.next();
				r = cp.compareTo(cp2);
			}
		}
		
		return r;
	}

}
