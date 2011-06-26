package org.openstreetmap.gui.jmapviewer;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.gis.db.GisPoint;
import org.gis.db.Polygon;

/**
 * A simple implementation of a polygon
 *
 * @author Dirk Kirsten
 *
 */
public class MapMarkerPolygon{
    private Polygon polygon;
    private Color color;

    public MapMarkerPolygon(Polygon polygon) {
        this(null, polygon);
    }
    
    public MapMarkerPolygon(Color color, Polygon polygon) {
    	if (color == null) {
    		Random rand = new Random();
    		this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.5f);
    	} else {
    		this.color = color;
    	}
        this.polygon = polygon;
    }
    
    public Polygon getPolygon() {
    	return this.polygon;
    }

    public void paint(Graphics g, JMapViewer viewer) {
    	LinkedList<Point> points = new LinkedList<Point>();
    	LinkedList<GisPoint> polygonPoints = this.getPolygonList();
    	
    	Iterator<GisPoint> it2 = polygonPoints.iterator();
    	while (it2.hasNext()) {
    		org.postgis.Point polygonPoint = it2.next();
    		Point p = viewer.getMapPosition(polygonPoint.x, polygonPoint.y);
    		if (p != null)
    			points.add(p);
    	}
    	
    	if (points.size() == 0)
    		return;
    	
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
        
        if (polygon.getText() != null && !polygon.getText().isEmpty()) {
        	org.postgis.Point mass = polygon.getMass();
        	Point massCoord = viewer.getMapPosition(mass.x, mass.y);
        	
        	if (massCoord != null) {
	        	/* Draw the Text in the middle of the polygon */
	        	/* Find the size of string s in font f in the current Graphics context g. */
	        	FontMetrics fm   = g.getFontMetrics(g.getFont());
	        	java.awt.geom.Rectangle2D rect = fm.getStringBounds(polygon.getText(), g);
	
	        	int textHeight = (int)(rect.getHeight()); 
	        	int textWidth  = (int)(rect.getWidth());
	
	        	/* Center text horizontally and vertically */
	        	int x = massCoord.x - textWidth / 2;
	        	int y = massCoord.y - textHeight / 2  + fm.getAscent();
	
	        	g.drawString(polygon.getText(), x, y);  // Draw the string.
        	}
        }
        
    }
    
    public LinkedList<GisPoint> getPolygonList() {
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
