package org.gis.db;

import java.util.*;

import org.postgis.Point;

public class Polygon {

	private LinkedList<Point> ring;
	
	public Polygon(){
		
	}
	
	public void addPoint(Point point){
		ring.add(point);
	}
	
	public LinkedList<Point> getRing(){
		return this.ring;
	}
	
	public void setRing(Point[] points){
		this.ring = new LinkedList<Point>(Arrays.asList(points));
	}
}
