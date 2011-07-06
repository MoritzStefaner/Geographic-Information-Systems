package org.gis.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.gis.db.*;

public class ImportTool {
	private Database db = null;

	/**
	 * @param args
	 */

	public ImportTool() {
		this.db = Database.getDatabase();
	}

	private void importStorks() {
		this.db.executeUpdate("drop table if exists storks");

		this.db.executeUpdate(
				"CREATE TABLE storks (" +
				"	id int PRIMARY KEY," +
				"	timestamp time," +
				"	altitude int," +
				"	tagLocalIdentifier int," +
				"	world_id int)"
				);
		
		this.db.executeQuery("SELECT AddGeometryColumn('','storks','geometrycolumn','-1','POINT',2);");
		
		String insert = new String("INSERT INTO storks (id, timestamp, altitude, tagLocalIdentifier, geometrycolumn, world_id) VALUES ");
		File file = new File("MPIO_White_Stork_Argos.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line = null;
		String[] token = null;
		String newInsert = null;
		int i = 0;

		// read each line of text file
		try {
			bufRdr.readLine();
			while ((line = bufRdr.readLine()) != null) {
				if (i != 0) {
					insert = insert.concat(",");
				}
				token = line.split(",");
				
				/* Get country where this point belongs to */
				ResultSet result = db.executeQuery("SELECT DISTINCT new_world.id FROM (SELECT Collect(the_geom) As the_geom, bla.id FROM (SELECT (DumpRings(poly_geom)).geom As the_geom, id FROM world) As bla GROUP BY bla.id) As new_world WHERE Contains(new_world.the_geom,  GeomFromText('POINT(" + token[2] + " " + token[1] + ")'))");
		    	
				int world_id = 0;
		    	try {
					if (result.next())
						world_id = (Integer) result.getObject(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				newInsert = new String("(" + String.valueOf(i) + ", '" + token[0] + "', " + token[4] + ", " + token[25].replace("\"", "") + ", GeomFromText('POINT(" + token[2] + " " + token[1] + ")')" + ", " + world_id + ")");
				insert = insert.concat(newInsert);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert = insert.concat(";");

		// System.out.println(insert);
		this.db.executeUpdate(insert);

	}
	
	private void importMalte() {
		this.db.executeUpdate("drop table if exists malte");

		this.db.executeUpdate(
				"CREATE TABLE malte (" +
				"	id int PRIMARY KEY," +
				"	startTime time," +
				"	endTime time," +
				"	service text," +
				"	inOutgoing text," +
				"	direction int," +
				"   cellA text," +
				"   cellB text," +
				"   wkr_nr int)"
				);
		
		this.db.executeQuery("SELECT AddGeometryColumn('','malte','geometrycolumn','-1','POINT',2);");
		
		String insert = new String("INSERT INTO malte (id, startTime, endTime, service, inOutgoing, direction, cellA, cellB, geometrycolumn, wkr_nr) VALUES ");
		File file = new File("GermanPolitician.csv");
		BufferedReader bufRdr = null;
		
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} 
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String line = null;
		String[] token = null;
		String newInsert = null;
		String[] date = null;
		int i = 0;
		
		//read each line of text file
		try {
			bufRdr.readLine();
			while((line = bufRdr.readLine()) != null) {	
				token = line.split(";");
				if (token.length == 9 && !token[4].isEmpty() && !token[5].isEmpty()) {
					if (i != 0) {
						insert = insert.concat(",");
					}
					if (token[6].isEmpty()) {
						token[6] = "NULL";
					}
					date = (token[0].split(" "))[0].split("/");
					if (date.length == 3)
						token[0] = "'20"+date[2]+"-"+date[0]+"-"+date[1]+" "+(token[0].split(" "))[1]+"'";
					else
						token[0] = "NULL";

					date = (token[1].split(" "))[0].split("/");
					if (date.length == 3)
						token[1] = "'20"+date[2]+"-"+date[0]+"-"+date[1]+" "+(token[1].split(" "))[1]+"'";
					else
						token[1] = "NULL";
					
					ResultSet result = db.executeQuery("SELECT wkr_nr FROM constituencies WHERE Contains(poly_geom, GeomFromText('POINT(" + token[5] + " " + token[4] + ")'))");
					Integer value = 0;
					
					try {
						result.next();
						value = (Integer) result.getObject(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					newInsert = new String("(" + String.valueOf(i) + ", " + token[0] + ", " + token[1] + ", '" + token[2] + "', '" + token[3] + "', " + token[6] + ", '" + token[7] + "', '" + token[8] + "', GeomFromText('POINT(" + token[5] + " " + token[4] + ")'), " + value + ")");

					insert = insert.concat(newInsert);
					i++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert = insert.concat(";");
		
		//System.out.println(insert);
		this.db.executeUpdate(insert);

	}
	
	private void importWorld() throws Exception{
		db.executeUpdate("drop table if exists world");
		this.db.executeUpdate("CREATE TABLE world (" 
				+ "id int PRIMARY KEY,"
				+ "	z bigint,"
				+ "	m bigint," 
				+ "	fips varchar," 
				+ "	iso2 varchar,"
				+ "	iso3 varchar," 
				+ "	un int," 
				+ "	name varchar,"
				+ "	area int," 
				+ "	pop2005 int," 
				+ "	region int,"
				+ "	subregion int," 
				+ "	lon double precision,"
				+ "	lat double precision)");
		
		db.executeQuery("SELECT AddGeometryColumn('','world','poly_geom', '-1','POLYGON',2)");

		Connection conn = db.getConn();
		PreparedStatement pst = conn.prepareStatement("INSERT INTO world (id, z, m, fips, iso2, iso3, un, name, area, pop2005, region, subregion, lon, lat, poly_geom) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,GeomFromText(?, -1))");
	
		
		File file = new File("TM_WORLD_BORDERS-0.3.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LinkedList<GisPoint> points = new LinkedList<GisPoint>();
		String line = null;
		String[] token = null;
		
		int j = 0;
		boolean firstCountry = true;
		int id = -1;
		String firstx = null;
		String firsty = null;
		String z = null;
		String m = null;
		String fips = null;
		String iso2 = null;
		String iso3 = null;
		String un = null;
		String name = null;
		String area = null;
		String pop2005 = null;
		String region = null;
		String subregion = null;
		String lon = null;
		String lat = null;
		Double maxDistance = 0.0;
		GisPoint maxPoint = null;
		
		bufRdr.readLine();
		while ((line = bufRdr.readLine()) != null) {
			token = line.split("\t");
			int newid = Integer.parseInt(token[0]);
			
			if (id == newid){				
				GisPoint point = new GisPoint(Double.parseDouble(token[2]), Double.parseDouble(token[1]));
					points.add(point);
	
			}else{
				
//				if(newid == 24){
//					System.out.println(maxPoint);
//				}
				
				if (firstCountry) {
					firstCountry = false;
				} else {
					pst.setInt(1, id);
					pst.setInt(2, Integer.parseInt(z));
					pst.setInt(3, Integer.parseInt(m));
					pst.setString(4, fips);
					pst.setString(5, iso2);
					pst.setString(6, iso3);
					pst.setInt(7, Integer.parseInt(un));
					pst.setString(8, name);
					pst.setInt(9, Integer.parseInt(area));
					pst.setInt(10, Integer.parseInt(pop2005));
					pst.setInt(11, Integer.parseInt(region));
					pst.setInt(12, Integer.parseInt(subregion));
					pst.setDouble(13, Double.parseDouble(lon));
					pst.setDouble(14, Double.parseDouble(lat));
					String polygon = "";				
					pst.setString(15, buildPolygon(points, id));
					pst.executeUpdate();
					System.out.println(id + ": " + name);
				}
				
				points = new LinkedList<GisPoint>();
				
				GisPoint point = new GisPoint(Double.parseDouble(token[2]), Double.parseDouble(token[1]));
				points.add(point);
				
				id = newid;
				z = token[3];
				m = token[4];
				fips = token[5];
				iso2 = token[6];
				iso3 = token[7];
				un = token[8];
				name = token[9];
				area = token[10];
				pop2005 = token[11];
				region = token[12];
				subregion = token[13];
				lon = token[14];
				lat = token[15];
			}
			j++;
		}
		pst.setInt(1, id);
		pst.setInt(2, Integer.parseInt(z));
		pst.setInt(3, Integer.parseInt(m));
		pst.setString(4, fips);
		pst.setString(5, iso2);
		pst.setString(6, iso3);
		pst.setInt(7, Integer.parseInt(un));
		pst.setString(8, name);
		pst.setInt(9, Integer.parseInt(area));
		pst.setInt(10, Integer.parseInt(pop2005));
		pst.setInt(11, Integer.parseInt(region));
		pst.setInt(12, Integer.parseInt(subregion));
		pst.setDouble(13, Double.parseDouble(lon));
		pst.setDouble(14, Double.parseDouble(lat));
		pst.setString(15, buildPolygon(points, id));	
		pst.executeUpdate();
		
		System.out.print("World: " + j + " Lines eingelesen!");		
	}
	
	private String buildPolygon(LinkedList<GisPoint> points, Integer id){
		
		String polygon = "POLYGON((";
		boolean first = true;
		LinkedList<GisPoint> rest = new LinkedList<GisPoint>();
		
		while (points.isEmpty() == false) {
			GisPoint firstPoint = points.removeFirst();
			Integer secIndex = points.indexOf(firstPoint);
			if (secIndex != -1) {
				
				if(first){
					polygon = polygon + firstPoint.x + " " + firstPoint.y;
					first = false;
				} else{
					polygon = polygon + ",(" + firstPoint.x + " " + firstPoint.y;
				}
				
				ListIterator<GisPoint> iterator = points.listIterator();
				while (secIndex>= 0) {
					GisPoint actPoint = iterator.next();
					iterator.remove();
					polygon = polygon + "," + actPoint.x + " " + actPoint.y;
					secIndex --;
				}
				polygon = polygon + ")";
			} else {
				rest.add(firstPoint);
//				if(first){
//					polygon = polygon + firstPoint.x + " " + firstPoint.y
//					+ "," + firstPoint.x + " " + firstPoint.y + ")";
//					first = false;
//				}else{
//					polygon = polygon + ",(" + firstPoint.x + " " + firstPoint.y
//							+ "," + firstPoint.x + " " + firstPoint.y + ")";
//				}
			}
		}	
		
		Double maxDistance = 0.0;
		GisPoint maxPoint = null;
		GisPoint lastPoint = null;
		
//		if(id == 23){
//			for(GisPoint point : rest){
//				if(lastPoint != null){
//					Double distance = point.compareTo(lastPoint);
//					if(maxDistance < distance){
//						maxDistance = distance;
//						maxPoint = point;
//					}
//				}
//			lastPoint = point;
//			}
//			System.out.println(maxPoint + ": " + maxDistance);
//		}
		
		ListIterator<GisPoint> iterator2 = rest.listIterator();
		if(rest.isEmpty() == false && iterator2.hasNext()){
			GisPoint startPoint = rest.getFirst();
			polygon = polygon + ",(" + startPoint.x + " " + startPoint.y;
			while(iterator2.hasNext()){
				GisPoint point = iterator2.next();
				polygon = polygon + "," + point.x + " " + point.y;
			}
			polygon = polygon + "," + startPoint.x + " " + startPoint.y + ")";
		}
		
		polygon = polygon + ")";
		return polygon;
	}
	
	private void importConstituencies() throws Exception{
		db.executeUpdate("drop table if exists constituencies");
		this.db.executeUpdate("CREATE TABLE constituencies ("
				+ "id int PRIMARY KEY,"
				+ "	partid int,"
				+ " perimeter double precision," 
				+ "	wkr_nr int," 
				+ "	wkr_name varchar,"
				+ "	land_nr int," 
				+ "	land_name varchar," 
				+ "	flag int)");
		
		db.executeQuery("SELECT AddGeometryColumn('','constituencies','poly_geom', '-1','POLYGON',2)");
		
		Connection conn = db.getConn();
		PreparedStatement pst = conn.prepareStatement("INSERT INTO constituencies (id, partid, perimeter, wkr_nr, wkr_name, land_nr, land_name, flag, poly_geom) VALUES (?,?,?,?,?,?,?,?,GeomFromText(?, -1))");
		
		File file = new File("Geometrie_Wahlkreise_17DBT.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String line = null;
		String[] token = null;
		
		int j = 0;
		boolean first = true;
		String polygon = "POLYGON((";
		int id = 0;
		String firstx = null;
		String firsty = null;
		String partid = null;
		String perimeter = null;
		String wkr_nr = null;
		String wkr_name = null;
		String land_nr = null;
		String land_name = null;
		String flag = null;	

		// read each line of text file
		try {
			bufRdr.readLine();
			while ((line = bufRdr.readLine()) != null) {
				token = line.split(";");
				int newid = Integer.parseInt(token[2]);
				
				if (id == newid){
					if(first){
						polygon = polygon + token[1] + " " + token[0];
						firstx = token[1];
						firsty = token[0];
						partid = token[3];
						perimeter = token[4];
						wkr_nr = token[5];
						wkr_name = token[6];
						land_nr = token[7];
						land_name = token[8];
						flag = token[9];
						first = false;
					}
					polygon = polygon + "," + token[1] + " " + token[0];
				}else{
					polygon = polygon + "," + firstx + " " + firsty + "))";
					pst.setInt(1, id);
					pst.setInt(2, Integer.parseInt(partid));
					pst.setDouble(3, Double.parseDouble(perimeter));
					pst.setInt(4, Integer.parseInt(wkr_nr));
					pst.setString(5, wkr_name);
					pst.setInt(6, Integer.parseInt(land_nr));
					pst.setString(7, land_name);
					pst.setInt(8, Integer.parseInt(flag));
					pst.setString(9, polygon);
					pst.executeUpdate();
					
					firstx = token[1];
					firsty = token[0];
					partid = token[3];
					perimeter = token[4];
					wkr_nr = token[5];
					wkr_name = token[6];
					land_nr = token[7];
					land_name = token[8];
					flag = token[9];
					
					id = newid;
					polygon = "POLYGON((" + token[1] + " " + token[0];
				}
				
				j++;
			}

			polygon = polygon + "," + firstx + " " + firsty + "))";
			pst.setInt(1, id);
			pst.setInt(2, Integer.parseInt(partid));
			pst.setDouble(3, Double.parseDouble(perimeter));
			pst.setInt(4, Integer.parseInt(wkr_nr));
			pst.setString(5, wkr_name);
			pst.setInt(6, Integer.parseInt(land_nr));
			pst.setString(7, land_name);
			pst.setInt(8, Integer.parseInt(flag));
			pst.setString(9, polygon);
			pst.executeUpdate();
			System.out.print("Constituencies: " + j + " Lines eingelesen!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void importResults() throws Exception{
		File file = new File("kerg.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));}
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Connection conn = db.getConn();
		conn.setAutoCommit(false);
		
		db.executeUpdate("drop table if exists results_const");
		db.executeUpdate("drop table if exists results_states");
		db.executeUpdate("drop table if exists parties");
		db.executeUpdate("drop table if exists voter_const");
		db.executeUpdate("drop table if exists voter_states");
		
		db.executeUpdate("CREATE TABLE parties ("
				+ " id int PRIMARY KEY,"
				+ "	name varchar)");

		
		PreparedStatement pstParties = conn.prepareStatement("INSERT INTO parties (id, name) VALUES (?,?)"); 

		db.executeUpdate("CREATE TABLE voter_states ("
				+ " id int PRIMARY KEY,"
				+ "	name varchar,"
				+ " part_of int," 
				+ "	elective_cur int," 
				+ "	elective_prev int,"
				+ "	voter_cur int," 
				+ "	voter_prev int)");

		PreparedStatement pstVoterStates = conn.prepareStatement("INSERT INTO voter_states (id, name, part_of, elective_cur, elective_prev, voter_cur, voter_prev) VALUES (?,?,?,?,?,?,?)"); 
		
		db.executeUpdate("CREATE TABLE voter_const ("
				+ " id int PRIMARY KEY,"
				+ "	name varchar,"
				+ " part_of int," 
				+ "	elective_cur int," 
				+ "	elective_prev int,"
				+ "	voter_cur int," 
				+ "	voter_prev int)");

		PreparedStatement pstVoterConst = conn.prepareStatement("INSERT INTO voter_const (id, name, part_of, elective_cur, elective_prev, voter_cur, voter_prev) VALUES (?,?,?,?,?,?,?)");
		
		db.executeUpdate("CREATE TABLE results_const ("
				+ " constituency_id int,"
				+ "	partie_id int," 
				+ " first_cur int," 
				+ "	first_prev int,"
				+ "	second_cur int," 
				+ "	second_prev int,"
				+ " FOREIGN KEY (constituency_id) REFERENCES voter_const (id)," 
				+ " FOREIGN KEY (partie_id) REFERENCES parties (id))");
		
		PreparedStatement pstResultConst = conn.prepareStatement("INSERT INTO results_const (constituency_id, partie_id, first_cur, first_prev, second_cur, second_prev) VALUES (?,?,?,?,?,?)");
		
		db.executeUpdate("CREATE TABLE results_states ("
				+ " state_id int,"
				+ "	partie_id int," 
				+ " first_cur int," 
				+ "	first_prev int,"
				+ "	second_cur int," 
				+ "	second_prev int,"
				+ " FOREIGN KEY (state_id) REFERENCES voter_states (id)," 
				+ " FOREIGN KEY (partie_id) REFERENCES parties (id))");
		
		PreparedStatement pstResultStates = conn.prepareStatement("INSERT INTO results_states (state_id, partie_id, first_cur, first_prev, second_cur, second_prev) VALUES (?,?,?,?,?,?)");
		
		String line = null;
		String[] token;
		int partieID = 0;
		
		try{
			bufRdr.readLine();
			bufRdr.readLine();
			line = bufRdr.readLine();
			token = line.split(";");
			for(int i = 11; i<token.length-1; i= i+4){
				pstParties.setInt(1, partieID);
				pstParties.setString(2, token[i]);
				pstParties.executeUpdate();
				partieID++;
			}
			
			bufRdr.readLine();
			bufRdr.readLine();
			
			while((line = bufRdr.readLine()) != null){
				token = line.split(";", -2);
				
				if (token[0].equals("")) { }else {
					// Sorts states out of constituencies.
					if ( ((Integer.parseInt(token[0])) == 99 && token[2].equals("")) || Integer.parseInt(token[2]) == 99) {
						pstVoterStates.setInt(1, Integer.parseInt(token[0]));
						pstVoterStates.setString(2, token[1]);
						if((Integer.parseInt(token[0])) == 99){
							pstVoterStates.setInt(3, Integer.parseInt(token[0]));
						} else{
							pstVoterStates.setInt(3, Integer.parseInt(token[2]));
							
						}
						pstVoterStates.setInt(4, Integer.parseInt(token[3]));
						pstVoterStates.setInt(5, Integer.parseInt(token[4]));
						pstVoterStates.setInt(6, Integer.parseInt(token[7]));
						pstVoterStates.setInt(7, Integer.parseInt(token[8]));
						pstVoterStates.executeUpdate();
						
						// Sets the results per partie by partieID.
						partieID = 0;
						for (int i = 11; i < token.length-1; i = i + 4) {
		
							if (partieID < 30) {
								pstResultStates.setInt(1,Integer.parseInt(token[0]));
								pstResultStates.setInt(2, partieID);
								if (token[i].equals("")) {
									pstResultStates.setInt(3, 0);
								} else {
									pstResultStates.setInt(3, Integer.parseInt(token[i]));
								}
								if (token[i + 1].equals("")) {
									pstResultStates.setInt(4, 0);
								} else {
									pstResultStates.setInt(4, Integer.parseInt(token[i + 1]));
								}
								if (token[i + 2].equals("")) {
									pstResultStates.setInt(5, 0);
								} else {
									pstResultStates.setInt(5, Integer.parseInt(token[i + 2]));
								}
								if (token[i + 3].equals("")) {
									pstResultStates.setInt(6, 0);
								} else {
									pstResultStates.setInt(6, Integer.parseInt(token[i + 3]));
								}
								pstResultStates.executeUpdate();
								partieID++;
							}
						}
						
						// Commits after the id of the state was setted, because of its foreign key.
						conn.commit();

					} else {
						pstVoterConst.setInt(1, Integer.parseInt(token[0]));
						pstVoterConst.setString(2, token[1]);
						pstVoterConst.setInt(3, Integer.parseInt(token[2]));
						pstVoterConst.setInt(4, Integer.parseInt(token[3]));
						pstVoterConst.setInt(5, Integer.parseInt(token[4]));
						pstVoterConst.setInt(6, Integer.parseInt(token[7]));
						pstVoterConst.setInt(7, Integer.parseInt(token[8]));
						pstVoterConst.executeUpdate();
						
						// Sets the results per partie by partieID.
						partieID = 0;
						for (int i = 11; i < token.length-1; i = i + 4) {
		
							if (partieID < 30) {
								pstResultConst.setInt(1,
										Integer.parseInt(token[0]));
								pstResultConst.setInt(2, partieID);
								if (token[i].equals("")) {
									pstResultConst.setInt(3, 0);
								} else {
									pstResultConst.setInt(3, Integer.parseInt(token[i]));
								}
								if (token[i + 1].equals("")) {
									pstResultConst.setInt(4, 0);
								} else {
									pstResultConst.setInt(4,
											Integer.parseInt(token[i + 1]));
								}
								if (token[i + 2].equals("")) {
									pstResultConst.setInt(5, 0);
								} else {
									pstResultConst.setInt(5,
											Integer.parseInt(token[i + 2]));
								}
								if (token[i + 3].equals("")) {
									pstResultConst.setInt(6, 0);
								} else {
									pstResultConst.setInt(6,
											Integer.parseInt(token[i + 3]));
								}
								pstResultConst.executeUpdate();
								partieID++;
							}
						}
					}
				}
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Results imported.");
		
		conn.setAutoCommit(true);
	}

	public static void main(String[] args) throws Exception {
		ImportTool it = new ImportTool();
		//it.importWorld();
		it.importStorks();
		//it.importConstituencies();
		//it.importMalte();
		//it.importResults();
	}

}
