package org.gis.data.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.data.GisPoint;
import org.gis.data.GisPoint.PointRelation;
import org.gis.data.Polygon.PolygonRelation;
import org.postgis.LinearRing;
import org.postgis.Point;

/**
 * This is a country.
 * 
 * @author Stephanie Marx
 * @author Dirk Kirsten
 *
 */
public class Country {
	private String fips;
	private String iso2;
	private String iso3;
	private String name;
	
	private Integer id;
	private Integer un;
	private Integer area;
	private Integer pop2005;
	private Integer region;
	private Integer subregion;
	private CountryPolygon polygon;
	private LinkedList<CountryPolygon> polygons;
	
	private int amountStorks;
	
	public Country(Integer id, String fips, String iso2, String iso3, Integer un, String name, Integer area, Integer pop2005, Integer region, Integer subregion){
		this.fips = fips;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.name = name;
		
		this.id = id;
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
	
	public Integer getId(){
		return this.id;
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
	
	public CountryPolygon getPolygon(){
		return this.polygon;
	}
	
	public void addPolygons(LinkedList<CountryPolygon> list){
		this.polygons = list;
		this.polygon = toOnePolygon();
	}
	
	public void setColor(Color c) {
		Iterator<CountryPolygon> it = getPolygons().iterator();
		
		while (it.hasNext()) {
			CountryPolygon cp = it.next();
			cp.setColor(c);
		}
	}
	
	public PointRelation compareToPoint(GisPoint p) {		
		return p.compareToCountry(polygons);
	}
	
	/**
	 * Returns the relationship between the Point and the 
	 * Country regarding to the 9-cut model
	 * 
	 * @param c Country to compare to
	 * @return PolygonRelationship between the Point and the Country
	 */
	public PolygonRelation compareToCountry(Country c) {
		Iterator<CountryPolygon> it = c.getPolygons().iterator();
		Iterator<CountryPolygon> it2 = getPolygons().iterator();
		PolygonRelation r = PolygonRelation.DISJOINT;
		
		while (it.hasNext()) {
			CountryPolygon cp = it.next();
			while (it2.hasNext()) {
				CountryPolygon cp2 = it2.next();
				r = cp.compareToPolygon(cp2);
			}
		}
		
		return r;
	}

	/**
	 * Returns an information string about this country.
	 * 
	 * @return Textual Information about this country
	 */
	public String getInformation() {
		return "Name: " + getName() + "\n" +
			   "Region: " + getRegion() + "\n" +
			   "Subregion: " + getSubregion() + "\n" +
			   "ISO 2:" + getIso2() + "\n" +
			   "ISO 3:" + getIso3() + "\n";
	}
	
	/**
	 * Made the single polygons to one big polygon to make them comparable. 
	 * @return
	 */
	private CountryPolygon toOnePolygon(){
		ArrayList<Point> points = new ArrayList<Point>();
		Point firstPoint = null;
		boolean first = true;
		
		for(CountryPolygon polygon : polygons){
			for(Point point : polygon.getRing()){
				if(first){
					firstPoint = point;
					first = false;
				}
				points.add(point);
			}
		}
		points.add(firstPoint);
		
		Point[] ringPoints = points.toArray(new Point[points.size()]);
		LinearRing ring = new LinearRing(ringPoints);
		LinearRing[] rings = {ring};
		CountryPolygon polygon = new CountryPolygon(this, rings);
		
		return polygon;
	}
}
