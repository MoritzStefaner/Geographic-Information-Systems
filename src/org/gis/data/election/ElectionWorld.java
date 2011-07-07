package org.gis.data.election;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.data.GisPoint;
import org.gis.tools.Database;
import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class ElectionWorld {
	private HashMap<Integer, Constituency> constituencyMap;
	private LinkedList<MapMarkerPolygon> drawList;
	private HashMap<Integer, MaltePoint> maltePoints;
	private float maxGreenParty = 0;
	private float minGreenParty;
	private MaltePoint lastPoint;
	private Constituency lastPolygon;
	
	public MaltePoint getLastPoint() {
		return lastPoint;
	}

	public void setLastPoint(MaltePoint lastPoint) {
		this.lastPoint = lastPoint;
	}

	public Constituency getLastPolygon() {
		return lastPolygon;
	}

	public void setLastPolygon(Constituency lastPolygon) {
		this.lastPolygon = lastPolygon;
	}

	public ElectionWorld() {
		constituencyMap = ExportObjectsTool.exportElection2009();
		drawList = getElectionPolygons(this.constituencyMap);
		maltePoints = ExportObjectsTool.exportMalte(); 
	}
	
    private LinkedList<MapMarkerPolygon> getElectionPolygons(HashMap<Integer, Constituency> constituencies) {
    	LinkedList<MapMarkerPolygon> drawList = new LinkedList<MapMarkerPolygon>();
    	Iterator<Constituency> it = constituencies.values().iterator();
    	Constituency constituency;
    	
    	while (it.hasNext()) {
    		constituency = it.next();
    		drawList.addAll(constituency.getPolygons());
    	}
    	
    	return drawList;
    }

	public HashMap<Integer, Constituency> getConstituencyMap() {
		return constituencyMap;
	}

	public LinkedList<MapMarkerPolygon> getDrawList() {
		return drawList;
	}
	
	public HashMap<Integer, MaltePoint> getMaltePoints() {
		return maltePoints;
	}
    
	public MaltePoint getMaltePoint(double longitude, double latitude) {
		GisPoint p = new GisPoint(latitude, longitude);
		Database db = Database.getDatabase();
		
		ResultSet result = db.executeQuery("SELECT id FROM malte ORDER BY DISTANCE(GeomFromText('" + p + "'), geometrycolumn) LIMIT 1");
		
		try {
			if (result.next())
				return maltePoints.get((Integer) result.getObject(1));
			else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public Constituency getConstituency(double longitude, double latitude) {
    	GisPoint p = new GisPoint(latitude, longitude);
    	Constituency c = getConstituencyMap().get(compareToGermany(p));
    	return c;
    }
    
	/**
	 * Relates a point to the countries in the world.
	 * 
	 * @return The position of the point as country id.
	 */
	public Integer compareToGermany(GisPoint point){
		Database db = Database.getDatabase();
		
		ResultSet result = db.executeQuery("SELECT wkr_nr FROM constituencies WHERE Contains(poly_geom, GeomFromText('"+point+"'))");
		
		try {
			if (result.next())
				return (Integer) result.getObject(1);
			else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	private void setGreenPartyExtrema() {
		if (maxGreenParty == 0) {
			Iterator<Constituency> it = getConstituencyMap().values().iterator();
			minGreenParty = 1;
			
			while (it.hasNext()) {
				Constituency c = it.next();
				HashMap<String, PartyResults> partyResults = c.getResult();
				
				if (partyResults != null) {
					PartyResults greenPartyResults = partyResults.get("GRÜNE");
					if (greenPartyResults != null) {
						float percentage = greenPartyResults.getZweitstimmen() / (float)c.getVoter();
						if (percentage > maxGreenParty) 
							maxGreenParty = percentage;
						if (percentage < minGreenParty)
							minGreenParty = percentage;
					}
				}
			}
		}
	}
	
	public void setColorBySize(int coefficient){
		Database db = Database.getDatabase();
		
		ResultSet result = db.executeQuery("SELECT wkr_nr, SUM(ST_AREA(poly_geom)) as area FROM constituencies GROUP BY wkr_nr ORDER BY area DESC");
		
		double largestConstituencySize = 0;
		try {
			while (result.next()) {
				Constituency c = getConstituencyMap().get((Integer) result.getObject(1));
				double size = (Double) result.getObject(2);
				if (largestConstituencySize == 0)
					largestConstituencySize = size;
				float alpha = (float) Math.pow(size / (float) largestConstituencySize, 1 / (float) coefficient);
				c.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setColorByGreenParty(int coefficient) {
		setGreenPartyExtrema();
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			HashMap<String, PartyResults> partyResults = c.getResult();

			if (partyResults != null) {
				PartyResults resultGreenParty = partyResults.get("GRÜNE");
				if (resultGreenParty != null) {
					Iterator<ConstPolygon> it3 = c.getPolygons().iterator();
					float alpha = (float) Math.pow((resultGreenParty.getZweitstimmen() / (float)c.getVoter() - minGreenParty) / (maxGreenParty - minGreenParty), 1 / (float) coefficient);
					
					while (it3.hasNext()) {
						MapMarkerPolygon m = it3.next();
						m.setColor(new Color((float) ((1 - alpha)*0.6 + 0.4), 1.0f, 1 - alpha, 0.8f));
					}
				}
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByInfluence(int coefficient) {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		int maxVoter = 0;
		int allVoters = 0;
		int minVoter = 100000000;
		while (it.hasNext()) {
			Constituency c = it.next();
			allVoters += c.getVoter();
			if (c.getVoter() > maxVoter)
				maxVoter = c.getVoter();
			if (c.getVoter() < minVoter)
				minVoter = c.getVoter();
		}
		
		it = getConstituencyMap().values().iterator();
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<ConstPolygon> it2 = c.getPolygons().iterator();
			float alpha = (float) Math.pow((c.getVoter() - minVoter) / (float) (maxVoter - minVoter), 1 / (float) coefficient);
			
			while (it2.hasNext()) {
				ConstPolygon m = it2.next();
				m.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByTurnout(int coefficient) {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			float alpha = (float) Math.pow(Math.max(c.getVoter() / (float) c.getElectorate() - 0.6, 0.0f) / 0.2, 1 / (float) coefficient);
			
			c.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}	
	
	public void setColorByDifference(int coefficient) {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<PartyResults> it2 = c.getResult().values().iterator();
			
			PartyResults winner = null;
			float winnerPercentage = 0.0f;
			float secondPercentage = 0.0f;
			
			PartyResults p1 = it2.next();
			PartyResults p2 = it2.next();
			if (p1.getZweitstimmen() / (float) c.getVoter() > p2.getZweitstimmen() / (float) c.getVoter()) {
				winner = p1;
				winnerPercentage = p1.getZweitstimmen() / (float) c.getVoter();
				secondPercentage = p2.getZweitstimmen() / (float) c.getVoter();
			} else {
				winner = p2;
				winnerPercentage = p2.getZweitstimmen() / (float) c.getVoter();
				secondPercentage = p1.getZweitstimmen() / (float) c.getVoter();
			}
			while (it2.hasNext()) {
				PartyResults p = it2.next();
				float percentage = p.getZweitstimmen() / (float) c.getVoter();
				if (percentage > winnerPercentage) {
					secondPercentage = winnerPercentage;
					winnerPercentage = percentage;
					winner = p;
				} else if (percentage > secondPercentage) {
					secondPercentage = percentage;
				}
			}
			
			/* Set color */
			float alpha = (float) Math.pow(Math.min((winnerPercentage - secondPercentage) * 10, 1.0f) * 0.8f, 1 / (float) coefficient);
			Color col = getColorAlpha(winner.getColor(), (byte) (alpha*255));
			c.setColor(col);
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByWinner() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<PartyResults> it2 = c.getResult().values().iterator();
			
			PartyResults winner = null;
			float winnerPercentage = 0.0f;
			while (it2.hasNext()) {
				PartyResults p = it2.next();
				float percentage = p.getZweitstimmen() / (float) c.getVoter();
				if (percentage > winnerPercentage) {
					winnerPercentage = percentage;
					winner = p;
				}
			}
			
			/* Set color */
			Color col = getColorAlpha(winner.getColor(), (byte) 0xBF);
			c.setColor(col);
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByGreenPartyCorrMalte(int coefficient) {
    	Database db = Database.getDatabase();
    	setGreenPartyExtrema();
    	
		ResultSet result = db.executeQuery("SELECT constituencies.wkr_nr, COUNT(malte.id) FROM constituencies, malte WHERE constituencies.wkr_nr = malte.wkr_nr GROUP BY constituencies.wkr_nr");
    	
		try {
			while(result.next()) {
				try {
					Constituency c = constituencyMap.get((Integer) result.getObject(1));
					c.addMalteOccurence(((Long) result.getObject(2)).intValue());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		while (it.hasNext()) {
			Constituency c = it.next();
			HashMap<String, PartyResults> partyResults = c.getResult();

			if (partyResults != null) {
				PartyResults greenPartyResult = partyResults.get("GRÜNE");
				if (greenPartyResult != null) {
					float expected = (maxGreenParty / 500) * c.getMalteOccurences();
					if (expected > 1)
						expected = 1;
					float actual = ((greenPartyResult.getZweitstimmen() / (float) c.getVoter() - minGreenParty) / (maxGreenParty - minGreenParty));
					float alpha = (float) Math.pow(Math.abs((actual - expected)), 1 / (float) coefficient);
					
					c.setColor(new Color(alpha, 1 - alpha, 0.0f, 0.9f));
				}
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	/**
	 * Hard task e.
	 * 
	 * Calculates all constituencies through which Malte did travel.
	 * 
	 * @return An wkrNr list of the constituencies.
	 */
    public LinkedList<Integer> getMalteTravel(){
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	Database db = Database.getDatabase();
    	
    	ResultSet result = db.executeQuery("SELECT DISTINCT constituencies.wkr_nr FROM constituencies, malte WHERE constituencies.wkr_nr = malte.wkr_nr");
    	
    	try {
			while(result.next()){
				list.add((Integer) result.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	return list;
    }
    
    private Color getColorAlpha(Color c, byte newAlpha) {
    	return new Color((c.getRGB() & ((newAlpha << 24) | 0x00ffffff)), true);
    }
	
}