package org.openstreetmap.gui.jmapviewer;

import java.awt.Color;
import java.awt.FontMetrics;
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
        this(new Color (0xFF, 0, 0, 0x33), polygon);
    }
    
    public MapMarkerPolygon(Color color, Polygon polygon) {
        this.color = color;
        this.polygon = polygon;
    }

    public void paint(Graphics g, JMapViewer viewer) {
    	LinkedList<Point> points = new LinkedList<Point>();
    	LinkedList<org.postgis.Point> polygonPoints = this.getPolygonList();
    	
    	Iterator<org.postgis.Point> it2 = polygonPoints.iterator();
    	while (it2.hasNext()) {
    		org.postgis.Point polygonPoint = it2.next();
    		points.add(viewer.getMapPosition(polygonPoint.x, polygonPoint.y));
    	}
    	
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
        
        if (!polygon.getText().isEmpty()) {
        	org.postgis.Point mass = polygon.getMass();
        	Point massCoord = viewer.getMapPosition(mass.x, mass.y);
        	
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
