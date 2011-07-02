package org.gis.db;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.gis.tools.ExportObjectsTool;
import org.openstreetmap.gui.jmapviewer.MapMarkerPolygon;

public class ElectionWorld {
	private HashMap<Integer, Constituency> constituencyMap;
	private LinkedList<MapMarkerPolygon> drawList;
	private float maxGreenParty;
	private float minGreenParty;
	
	public ElectionWorld() {
		this.constituencyMap = ExportObjectsTool.exportElection2009();
		this.drawList = getElectionPolygons(this.constituencyMap);

		setGreenPartyExtrema();
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
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		maxGreenParty = 0;
		minGreenParty = 1;
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			boolean found = false;
			while (it2.hasNext() && !found) {
				Party p = it2.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					float percentage = p.getZweitstimmen() / (float)c.getVoter();
					if (percentage > maxGreenParty) 
						maxGreenParty = percentage;
					if (percentage < minGreenParty)
						minGreenParty = percentage;
					
					found = true;
				}
			}
		}
	}
	
	public void setColorByGreenPartyLinear() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			boolean found = false;
			while (it2.hasNext() && !found) {
				Party p = it2.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					Iterator<ConstPolygon> it3 = c.getPolygons().iterator();
					float alpha = (p.getZweitstimmen() / (float)c.getVoter() - minGreenParty) / (maxGreenParty - minGreenParty);
					
					while (it3.hasNext()) {
						MapMarkerPolygon m = it3.next();
						m.setColor(new Color((float) ((1 - alpha)*0.6 + 0.4), 1.0f, 1 - alpha, 0.8f));
					}
					
					found = true;
				}
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	/*
	private void setColorByGreenPartyAndMalteExtrema(Collection<MaltePoint> malte) {
		Iterator<MaltePoint> it = malte.iterator();
		
		while (it.hasNext()) {
			MaltePoint m = it.next();
			Integer constituencyInt = m.compareToConstituencies();
			if (constituencyInt != null) {
				Constituency c = constituencyMap.get(constituencyInt);
				c.addMalteOccurence();
			}
		}
		
		Iterator<Constituency> it2 = getConstituencyMap().values().iterator();
		LinkedList<Constituency> topGreen = new LinkedList<Constituency>();
		
		while (it2.hasNext()) {
			Constituency c = it2.next();
			Iterator<Party> it3 = c.getResult().iterator();
			boolean found = false;
			while (it3.hasNext() && !found) {
				Party p = it3.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					if (topGreen.size() < 5) {
						topGreen.add(c);
					} else {
						Iterator<Constituency> it4 = topGreen.iterator();
						boolean found2 = false;
						
						while (it4.hasNext() && !found2) {
							Constituency c2 = it4.next();
							if (c2.)
						}
					}
					
					found = true;
				}
			}
		}
	}*/
	
	public void setColorByInfluence() {
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
			float alpha = (c.getVoter() - minVoter) / (float) (maxVoter - minVoter);
			
			while (it2.hasNext()) {
				ConstPolygon m = it2.next();
				m.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByTurnout() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<ConstPolygon> it2 = c.getPolygons().iterator();
			float alpha = (float) Math.min(Math.max(c.getVoter() / (float) c.getElectorate() - 0.6, 0.0f) / 0.2, 1.0f);
			
			while (it2.hasNext()) {
				ConstPolygon m = it2.next();
				m.setColor(new Color(1.0f, (float) ((1 - alpha)*0.6 + 0.4), 1 - alpha, 0.8f));
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}	
	
	public void setColorByDifference() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			
			Party winner = null;
			float winnerPercentage = 0.0f;
			float secondPercentage = 0.0f;
			
			Party p1 = it2.next();
			Party p2 = it2.next();
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
				Party p = it2.next();
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
			Iterator<ConstPolygon> it3 = c.getPolygons().iterator();
			
			float alpha = Math.min((winnerPercentage - secondPercentage) * 10, 1.0f) * 0.8f;
			Color col = null;
			if (winner.getName().equalsIgnoreCase("CDU") || winner.getName().equalsIgnoreCase("CSU"))
				col = new Color(0.0f, 0.0f, 0.0f, alpha);
			else if (winner.getName().equalsIgnoreCase("SPD"))
				col = new Color(1.0f, 0.0f, 0.0f, alpha);
			else if (winner.getName().equalsIgnoreCase("DIE LINKE"))
				col = new Color(1.0f, 0.0f, 1.0f, alpha);
			else if (winner.getName().equalsIgnoreCase("GRÜNE"))
				col = new Color(0.0f, 1.0f, 0.0f, alpha);
			else
				col = Color.GRAY;
			
			while (it3.hasNext()) {
				ConstPolygon m = it3.next();
				m.setColor(col);
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByWinner() {
		Iterator<Constituency> it = getConstituencyMap().values().iterator();
		
		while (it.hasNext()) {
			Constituency c = it.next();
			Iterator<Party> it2 = c.getResult().iterator();
			
			Party winner = null;
			float winnerPercentage = 0.0f;
			while (it2.hasNext()) {
				Party p = it2.next();
				float percentage = p.getZweitstimmen() / (float) c.getVoter();
				if (percentage > winnerPercentage) {
					winnerPercentage = percentage;
					winner = p;
				}
			}
			
			/* Set color */
			Iterator<ConstPolygon> it3 = c.getPolygons().iterator();
			
			Color col = null;
			if (winner.getName().equalsIgnoreCase("CDU") || winner.getName().equalsIgnoreCase("CSU"))
				col = new Color(0.0f, 0.0f, 0.0f, 0.75f);
			else if (winner.getName().equalsIgnoreCase("SPD"))
				col = new Color(1.0f, 0.0f, 0.0f, 0.75f);
			else if (winner.getName().equalsIgnoreCase("DIE LINKE"))
				col = new Color(1.0f, 0.0f, 1.0f, 0.75f);
			else if (winner.getName().equalsIgnoreCase("GRÜNE"))
				col = new Color(0.0f, 1.0f, 0.0f, 0.75f);
			else
				col = Color.GRAY;
			
			while (it3.hasNext()) {
				ConstPolygon m = it3.next();
				m.setColor(col);
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
	public void setColorByGreenPartyCorrMalte(Collection<MaltePoint> malte) {
		Iterator<MaltePoint> it = malte.iterator();
		
		while (it.hasNext()) {
			MaltePoint m = it.next();
			Integer constituencyInt = m.compareToConstituencies();
			if (constituencyInt != null) {
				Constituency c = constituencyMap.get(constituencyInt);
				c.addMalteOccurence();
			}
		}
		
		Iterator<Constituency> it2 = getConstituencyMap().values().iterator();
		while (it2.hasNext()) {
			Constituency c = it2.next();
			Iterator<Party> it3 = c.getResult().iterator();
			boolean found = false;
			while (it3.hasNext() && !found) {
				Party p = it3.next();
				if (p.getName().equalsIgnoreCase("GRÜNE")) {
					Iterator<ConstPolygon> it4 = c.getPolygons().iterator();
					float expected = (maxGreenParty / 500) * c.getMalteOccurences();
					if (expected > 1)
						expected = 1;
					float actual = ((p.getZweitstimmen() / (float) c.getVoter() - minGreenParty) / (maxGreenParty - minGreenParty));
					float alpha = 1 - Math.abs((actual - expected));
					float alpha2 = (float) Math.min(2*alpha, 1.0);
					
					while (it4.hasNext()) {
						ConstPolygon m = it4.next();
						m.setColor(new Color(1 - alpha2, alpha2, 0.0f, 0.9f));
					}
					
					found = true;
				}
			}
		}
		
		this.drawList = getElectionPolygons(this.constituencyMap);
	}
	
}