package org.gis.db;

import java.util.*;

import org.postgis.Point;

public abstract class Polygon {

	private LinkedList<Point> ring;

	public Polygon() {
		ring = new LinkedList<Point>();
	}
	
	public Polygon(Point[] polygon){
		setRing(polygon);
	}
	
	public LinkedList<Point> getRing(){
		return ring;
	}
	
	public void setRing(Point[] points){
		this.ring = new LinkedList<Point>(Arrays.asList(points));
	}
	
	public void addPoint(Point point) {
		getRing().add(point);
	}
	
	public Point getMass() {
		Point pNow;
		Point pNext;
		float areal = 0.0f;
		float Cx = 0.0f;
		float Cy = 0.0f;
		Iterator<Point> it = this.getRing().iterator();
		
		/* The ring is not yet filled, something is wrong */
		if (this.getRing() == null || this.getRing().size() == 0) {
			return null;
		}
		/* The polygon is just a single point */
		else if (this.getRing().size() == 1) {
			return this.getRing().getFirst();
		}
		/* The polygon is indeed a line */
		else if (this.getRing().size() == 2) {
			pNow = this.getRing().getFirst();
			pNext = this.getRing().getLast();
			
			return new Point((pNow.x + pNext.x) / 2, (pNow.y + pNext.y)/ 2);
		}
		/* Normal polygon with at least 3 edges */
		else {
			pNow = it.next();
			while (it.hasNext()) {
				pNext = it.next();
				areal += pNow.x * pNext.y - pNext.x * pNow.y;
				Cx += (pNow.x + pNext.x) * (pNow.x * pNext.y - pNext.x * pNow.y);
				Cy += (pNow.y + pNext.y) * (pNow.x * pNext.y - pNext.x * pNow.y);
				
				pNow = pNext;
			}
			/* Calculate one last time, because the Nth element is the first one again.
			 * The polygon is represented by a linked list, although it is in fact a ring,
			 * so this workaround is needed.
			 */
			pNext = this.getRing().getFirst();
			
			areal += pNow.x * pNext.y - pNext.x * pNow.y;
			Cx += (pNow.x + pNext.x) * (pNow.x * pNext.y - pNext.x * pNow.y);
			Cy += (pNow.y + pNext.y) * (pNow.x * pNext.y - pNext.x * pNow.y);
			
			Cx = Cx / (3 * areal);
			Cy = Cy / (3 * areal);
			
			return new Point(Cx, Cy);
		}
	}
	
	public abstract String getText();
}
