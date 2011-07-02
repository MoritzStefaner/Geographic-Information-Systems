package org.openstreetmap.gui.jmapviewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.gis.db.GisPoint;
import org.gis.db.Polygon;
import org.postgis.LinearRing;

/**
 * A simple implementation of a polygon
 *
 * @author Dirk Kirsten
 *
 */
public class MapMarkerPolygon extends Polygon {
	private static final long serialVersionUID = -4607579477142781251L;
    private Color color;
    
    public MapMarkerPolygon(LinearRing[] rings) {
        super(rings);
    }
    
    public MapMarkerPolygon(LinearRing[] rings, boolean random) {
    	super(rings);
    	if (random) {
	    	Random rand = new Random();
			this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.5f);
    	}
    }
    
    public MapMarkerPolygon(LinearRing[] rings, Color color) {
    	super(rings);
    	if (color == null) {
    		this.color = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    	} else {
    		this.color = color;
    	}
    }
    
    public void setColor(Color color) {
    	this.color = color;
    }

    public void paint(Graphics g, JMapViewer viewer, boolean names) {
    	LinkedList<Point> points = new LinkedList<Point>();
    	LinkedList<GisPoint> polygonPoints = this.getPolygonList();
    	
    	Iterator<GisPoint> it2 = polygonPoints.iterator();
    	boolean visible = false;
    	while (it2.hasNext()) {
    		org.postgis.Point polygonPoint = it2.next();
    		Point p = viewer.getMapPosition(polygonPoint.x, polygonPoint.y, false);
    		
    		if (!visible && (p.x > 0 && p.y > 0 && p.x < viewer.getWidth() && p.y < viewer.getHeight()))
    			visible = true;
    		
    		points.add(p);
    	}
    	
    	if (points.size() == 0 || !visible)
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
        g.drawPolygon(xPoints, yPoints, points.size());
        
        if (names && getText() != null && !getText().isEmpty()) {
        	org.postgis.Point mass = getMass();
        	Point massCoord = viewer.getMapPosition(mass.x, mass.y);
        	
        	if (massCoord != null) {
	        	/* Draw the Text in the middle of the polygon */
	        	/* Find the size of string s in font f in the current Graphics context g. */
        		Font font = new Font("Verdana", Font.PLAIN, 12);
        		g.setFont(font);
	        	FontMetrics fm   = g.getFontMetrics(g.getFont());
	        	java.awt.geom.Rectangle2D rect = fm.getStringBounds(getText(), g);
	
	        	int textHeight = (int)(rect.getHeight()); 
	        	int textWidth  = (int)(rect.getWidth());
	
	        	/* Center text horizontally and vertically */
	        	int x = massCoord.x - textWidth / 2;
	        	int y = massCoord.y - textHeight / 2  + fm.getAscent();
	
	        	g.drawString(getText(), x, y);  // Draw the string.
        	}
        }
        
    }
    
    public LinkedList<GisPoint> getPolygonList() {
    	return getRing();
    }

    public String getDescription() {
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

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return "Test";
	}

}
