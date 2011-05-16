package org.openstreetmap.gui.jmapviewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import org.gis.db.Polygon;

/**
 * A simple implementation of a polygon
 *
 * @author Dirk Kirsten
 *
 */
public class MapMarkerPolygon {
    private Polygon polygon;
    private Color color;

    public MapMarkerPolygon(Polygon polygon) {
        this(Color.YELLOW, polygon);
    }

    public MapMarkerPolygon(Color color, Polygon polygon) {
        this.color = color;
        this.polygon = polygon;
    }

    public void paint(Graphics g, LinkedList<Point> points) {
    	Iterator<Point> it = points.iterator();
    	int[] xPoints = new int[points.size()];
    	int[] yPoints = new int[points.size()];
        Point point;
        int i = 0;
        
        while (it.hasNext()) {
        	point = it.next();
        	xPoints[i] = point.x;
        	yPoints[i] = point.y;
        	++i;
        }
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, points.size());
        g.setColor(Color.BLACK);
    }
    
    public LinkedList<org.postgis.Point> getPolygonList() {
    	return this.polygon.getRing();
    }

    public String toString() {
        /*
    	String description = "Polygon at ";
        Iterator<Point> it = this.polygon.iterator();
        Point p;
        
        while (it.hasNext()) {
        	p = it.next();
        	description.concat(" (" + p.x + ", " + p.y + "),");
        }
        */
    	String description = "Polygon";
        return description;
    }

}
