package org.gis.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

import org.gis.db.*;
import org.postgis.*;

public class ExportObjectsTool {
	
	private Database db;

	public ExportObjectsTool(){
		this.db = new Database();
	}
	
	/**
	 * Exports the point dates of storks from the database.
	 * 
	 * @return The point dates as map.
	 */
	public HashMap<Integer, StorkPoint> exportStork(){
		// The SQL query to get the information from database.
		ResultSet result = db.executeQuery("select id, timestamp, altitude, taglocalidentifier, geometrycolumn from storks");
		HashMap<Integer, StorkPoint> pointMap = new HashMap<Integer, StorkPoint>();
		
		try {
			// Iterates over all lines of the ResultSet to put each line in the map.
			while(result.next()){
				
				PGgeometry geom = (PGgeometry) result.getObject(5);
				// Extracts the point dates from the ResultSet.
				GisPoint point = (GisPoint) geom.getGeometry();
				point.setZ((Integer) result.getObject(3));
				
				// Creates a new StorkPoint.
				StorkPoint spoint = new StorkPoint((Integer) result.getObject(1), (Time) result.getObject(2), (Integer) result.getObject(4), point);
				
				pointMap.put((Integer) result.getObject(1), spoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pointMap;	
	}
	
	/**
	 * Exports the position dates of Mr. Malte during the election in Germany in 2009.
	 * 
	 * @return A map with the positions as Points.
	 */
	public HashMap<Integer, MaltePoint> exportMalte(){
		// The SQL query to get the information from database.
		ResultSet result = db.executeQuery("select id, starttime, endtime, service, inoutgoing, direction, cella, cellb, geometrycolumn from malte");
		
		HashMap<Integer, MaltePoint> pointMap = new HashMap<Integer, MaltePoint>();
		
		try {
			// Iterates over all lines of the ResultSet to put each line in the map.
			while(result.next()){
				// Extracts the point dates from the ResultSet.
				PGgeometry geom = (PGgeometry) result.getObject(9);
				GisPoint point = (GisPoint) geom.getGeometry();
				
				// Creates a new MaltePoint-object.
				MaltePoint mpoint = new MaltePoint((Integer) result.getObject(1), (Time) result.getObject(2), (Time) result.getObject(3), 
						(String) result.getObject(4), (String) result.getObject(5), (Integer) result.getObject(6), (String) result.getObject(7), 
						(String) result.getObject(8), point);
				
				pointMap.put((Integer) result.getObject(1), mpoint);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pointMap;	
	}

	/**
	 * Exports the geometric dates of all countries of the world.
	 * 
	 * @return A map of all countries.
	 */
	public HashMap<Integer, WorldPolygon> exportWorld(){
		// The SQL query to get the information from database.
		ResultSet result = db.executeQuery("select id, fips, iso2, iso3, un, name, area, pop2005, region, subregion, poly_geom from world");
		
		HashMap<Integer, WorldPolygon> polygonMap = new HashMap<Integer, WorldPolygon>();
		
		try {
			// Iterates over all lines of the ResultSet to put each line in the map.
			while(result.next()){
				// Extracts the polygon dates from the ResultSet.
				PGgeometry geom = (PGgeometry) result.getObject(11);
				org.postgis.Polygon ngeom = (org.postgis.Polygon) geom.getGeometry();
				
				// Creates a new country. 
				WorldPolygon polygon = new WorldPolygon((String) result.getObject(2), (String) result.getObject(3), 
						(String) result.getObject(4), (Integer) result.getObject(5), (String) result.getObject(6), (Long) result.getObject(7),
						(Long) result.getObject(8), (Integer) result.getObject(9), (Integer) result.getObject(10), ngeom.getRing(0).getPoints());

				polygonMap.put((Integer) result.getObject(1), polygon);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return polygonMap;
	}
	
	/**
	 * Exports the election results of Germany in the year 2009 from database.
	 * 
	 * @return A map of all constituencies containing all information about the election.
	 */
	public HashMap<Integer, Constituency> exportElection2009(){
		// A map of all constituencies.
		HashMap<Integer, Constituency> map = new HashMap<Integer, Constituency>();
		// A map of all federal states.
		HashMap<Integer, FederalState> stateMap = new HashMap<Integer, FederalState>();

		FederalState curState;
		
		// Searches for all 299 constituencies.
		for(int i = 1; i < 300; i++){
			// The SQL queries to get the information from the database without joins, because the table will become too big and confusing.
			ResultSet constGeoResult = db.executeQuery("select poly_geom, wkr_name, land_nr, land_name from constituencies where wkr_nr ="+i);
			ResultSet constElecResult = db.executeQuery("select elective_cur, voter_cur from voter_const where id ="+i);
			
			LinkedList<ConstPolygon> polygons = new LinkedList<ConstPolygon>();
			
			try {
				// Sets the pointer of the ResulSet on row 1 to get information.
				constGeoResult.next();
				boolean isFirst = true;
				String constituencyName = (String) constGeoResult.getObject(2);
				
				// Checks if the states already exists at the stateMap or if its new to create an new object of it.
				if(stateMap.containsKey((Integer) constGeoResult.getObject(3))){
					curState = stateMap.get((Integer) constGeoResult.getObject(3));
				}else{
					curState = newFederalState((Integer) constGeoResult.getObject(3), (String) constGeoResult.getObject(4));
					stateMap.put(curState.getId(), curState);
				}
				
				// Iterates over the ResultSet with all polygons of a constituency from datatbase.
				while(isFirst || constGeoResult.next()){
					isFirst = false;
					PGgeometry geom = (PGgeometry) constGeoResult.getObject(1);
					org.postgis.Polygon ngeom = (org.postgis.Polygon) geom.getGeometry();
					ConstPolygon polygon = new ConstPolygon(ngeom.getRing(0).getPoints());
					polygons.add(polygon);
				}
					
				// Creates the constituency-object and adds it to its state and the map of all constituencies.
				constElecResult.next();
				Constituency constituency = new Constituency(i, constituencyName, (Integer) constElecResult.getObject(1), 
						(Integer) constElecResult.getObject(2), curState, getParties(i), polygons);
				curState.addConstituency(constituency);
				map.put(i, constituency);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;		
	}
	
	/**
	 * Creates a federal state with the complete election information per state.
	 * 
	 * @param id the id of the state.
	 * @param name the name of the state.
	 * @return a new FederalState-object.
	 */
	private FederalState newFederalState(Integer id, String name){
		LinkedList<Party> parties = new LinkedList<Party>();
		FederalState state = null;
		
		// The SQL-queries to get the information from database.
		ResultSet federalElecResult = db.executeQuery("select elective_cur, voter_cur from voter_states where id ="+id); 
		ResultSet federalVoteResult = db.executeQuery("select name, first_cur, second_cur " +
				"from results_states r join parties p on r.partie_id = p.id where state_id="+id);
		try {
			//Iterates over all lines of the ResultSet and generates the list of parties with their results.
			while(federalVoteResult.next()){
				Party party;
				
					party = new Party((String) federalVoteResult.getObject(1), (Integer) federalVoteResult.getObject(2), 
							(Integer) federalVoteResult.getObject(3));
				
				parties.add(party);
			}	
			
			federalElecResult.next();
			// Creates the new FederalState-object.
			state = new FederalState(id, name, (Integer) federalElecResult.getObject(1), (Integer) federalElecResult.getObject(2), parties);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return state;
	}
	
	/**
	 * Exports the results for a constituency as a LinkedList of parties an their voters.
	 * 
	 * @param i the constituency
	 * @return a LinkedList of parties
	 */
	private LinkedList<Party> getParties(int i){
		LinkedList<Party> parties = new LinkedList<Party>();
		
		// The SQL-Query to get the informations from the database by joining results_const with parties.
		ResultSet constVoteResult = db.executeQuery("select name, first_cur, second_cur " +
				"from results_const r join parties p on r.partie_id = p.id where constituency_id="+i);
		try {
			// Iterates over all lines of the ResulSet.
			while(constVoteResult.next()){
				Party curParty = new Party((String) constVoteResult.getObject(1), (Integer) constVoteResult.getObject(2), (Integer) constVoteResult.getObject(3));
				parties.add(curParty);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parties;
	}
}
