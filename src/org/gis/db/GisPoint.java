package org.gis.db;

import org.postgis.Point;

public abstract class GisPoint extends Point{
	
	protected void setPoint(GisPoint point){
		this.x = point.getX();
		this.y = point.getY();
		this.z = point.getZ();
	}
}
