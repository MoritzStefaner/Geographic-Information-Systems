package org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A simple implementation of a polygon
 *
 * @author Dirk Kirsten
 *
 */
public class MapMarkerPolygon {

    LinkedList<Point> polygon;
    Color color;

    public MapMarkerPolygon(LinkedList<Point> polygon) {
        this(Color.YELLOW, polygon);
    }

    public MapMarkerPolygon(Color color, LinkedList<Point> polygon) {
        this.color = color;
        this.polygon = polygon;
    }

    public void paint(Graphics g) {
    	Polygon p = new Polygon();
    	Iterator<Point> it = this.polygon.iterator();
        Point point;
        
        while (it.hasNext()) {
        	point = it.next();
        	p.addPoint(point.x, point.y);
        }
        
        g.setColor(color);
        g.fillPolygon(p);
        g.setColor(Color.BLACK);
    }

    public String toString() {
        String description = "Polygon at ";
        Iterator<Point> it = this.polygon.iterator();
        Point p;
        
        while (it.hasNext()) {
        	p = it.next();
        	description.concat(" (" + p.x + ", " + p.y + "),");
        }
        
        return description;
    }

}
