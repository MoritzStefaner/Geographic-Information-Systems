package org.gis.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.gis.db.GisPoint.Relation;
import org.postgis.*;

public abstract class Polygon extends org.postgis.Polygon {
	private static final long serialVersionUID = -5307565671537188424L;
	// The ring from org.postgis.Polygon as LinkedList.
	private LinkedList<GisPoint> ring;
	
	public Polygon(LinearRing[] rings){
		super(rings);
		setRing(this.getRing(0).getPoints());
	}
	
	public LinkedList<GisPoint> getRing(){
		return ring;
	}
	
	protected void setRing(Point[] points){
		this.ring = new LinkedList<GisPoint>();
		
		for(Point point : points){
			this.ring.add(new GisPoint(point));
		}
	}
	
	public void addPoint(GisPoint point) {
		getRing().add(point);
	}
	
	public Point getMass() {
		Point pNow;
		Point pNext;
		float areal = 0.0f;
		float Cx = 0.0f;
		float Cy = 0.0f;
		Iterator<GisPoint> it = this.getRing().iterator();
		
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
	
	/**
	 * Hard task b.
	 * 
	 * Describes the relation of this polygon to a point.
	 * 
	 * @param point
	 * @return The enumeration value of the relation.
	 */
	public Relation compareTo(GisPoint point){
		boolean value = false;
		Database db = Database.getDatabase();
		
		ResultSet contains = db.executeQuery("SELECT Contains(GeomFromText('"+this+"'), GeomFromText('"+point+"')) AS contains");
		try {
			contains.next();
			value = (Boolean) contains.getObject(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// If the point isn't in the this polygon test if the point touches it.
		if(value){
			return Relation.INSIDE;
		}else{
			ResultSet touches = db.executeQuery("SELECT ST_Touches('"+this+"'::geometry, '"+point+"'::geometry);");
			
			try {
				touches.next();
				value = (Boolean) touches.getObject(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(value){
				return Relation.BORDER;
			}	
		}
		
		return Relation.OUTSIDE;
	}
	
	/**
	 * Hard task c.
	 * 
	 * Describes the relation of this polygon with another one as DE-9IM matrix.
	 * 
	 * See more for interpretation of the result here: http://publib.boulder.ibm.com/infocenter/idshelp/v10/index.jsp?topic=/com.ibm.spatial.doc/spat122.htm
	 * 
	 * @param polygon
	 * @return the DE-9IM matrix as String
	 */
	public String compareTo(Polygon polygon){
		
		Database db = Database.getDatabase();
		String matrix = "";
		
		ResultSet result = db.executeQuery("SELECT Relate(GeometryFromText('"+this+"'), GeometryFromText('"+polygon+"'))");
		
		try {
			result.next();
			matrix = (String) result.getObject(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return matrix;	
	}
	
	public abstract String getText();
}
