package org.gis.importtool;

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
import java.sql.SQLException;

import org.gis.db.*;
import org.postgis.*;

public class ImportTool {
	private Database db = null;

	/**
	 * @param args
	 */

	public ImportTool() {
		this.db = new Database();
	}
	public void importStorks() {
		this.db.executeQuery(
				"CREATE TABLE storks (" +
				"	id bigint PRIMARY KEY," +
				"	timestamp time," +
				"	altitude bigint," +
				"	tagLocalIdentifier bigint)"
				);
		
		this.db.executeQuery("SELECT AddGeometryColumn('','storks','geometrycolumn','-1','POINT',2);");
		
		String insert = new String("INSERT INTO storks (id, timestamp, altitude, tagLocalIdentifier, geometrycolumn) VALUES ");
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
				newInsert = new String("(" + String.valueOf(i) + ", '" + token[0] + "', " + token[4] + ", " + token[25].replace("\"", "") + ", GeomFromText('POINT(" + token[1] + " " + token[2] + ")')" + ")");
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
	
	public void importMalte() {
//		this.db.executeQuery(
//				"CREATE TABLE malte (" +
//				"	id bigint PRIMARY KEY," +
//				"	startTime time," +
//				"	endTime time," +
//				"	service text," +
//				"	inOutgoing text," +
//				"	direction bigint," +
//				"   cellA text," +
//				"   cellB text)"
//				);
		
		String insert = new String("INSERT INTO malte (id, startTime, endTime, service, inOutgoing, direction, cellA, cellB) VALUES ");
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
			while((line = bufRdr.readLine()) != null && i < 10) {	
				if (i != 0) {
					insert = insert.concat(",");
				}
				token = line.split(",");
				if (token.length == 9) {
					if (token[6].isEmpty()) {
						token[6] = "NULL";
					}
					date = (token[0].split(" "))[0].split("/");
					if (date.length == 3)
						token[0] = "20"+date[2]+"-"+date[0]+"-"+date[1]+" "+(token[0].split(" "))[1];

					date = (token[1].split(" "))[0].split("/");
					if (date.length == 3)
						token[1] = "20"+date[2]+"-"+date[0]+"-"+date[1]+" "+(token[1].split(" "))[1];
					newInsert = new String("(" + String.valueOf(i) + ", '" + token[0] + "', '" + token[1] + "', '" + token[2] + "', '" + token[3] + "', " + token[6] + ", '" + token[7] + "', '" + token[8] + "')");
				}
				insert = insert.concat(newInsert);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert = insert.concat(";");
		
		System.out.println(insert);
		//this.db.executeQuery(insert);

	}

	public void importWorld() throws Exception {
		db.executeUpdate("drop table if exists world");
		this.db.executeUpdate("CREATE TABLE world (" 
				+ "id int PRIMARY KEY,"
				+ "	z bigint,"
				+ "	m bigint," 
				+ "	fips varchar," 
				+ "	iso2 varchar,"
				+ "	iso3 varchar," 
				+ "	un bigint," 
				+ "	name varchar,"
				+ "	area bigint," 
				+ "	pop2005 bigint," 
				+ "	region bigint,"
				+ "	subregion bigint," 
				+ "	lon double precision,"
				+ "	lat double precision)");
		
		db.executeQuery("SELECT AddGeometryColumn('','world','poly_geom', '-1','POLYGON',2)");

		Connection conn = db.getConn();
		PreparedStatement pst = conn.prepareStatement("INSERT INTO world (id, z, m, fips, iso2, iso3, un, name, area, pop2005, region, subregion, lon, lat, poly_geom) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,GeomFromText(?, -1))");
	
		
		File file = new File("TM_WORLD_BORDERS-0.3.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-16"));
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

		// read each line of text file
		try {
			bufRdr.readLine();
			while ((line = bufRdr.readLine()) != null) {
				token = line.split("\t");
				int newid = Integer.parseInt(token[0]);
				
				if (id == newid){
					if(first){
						polygon = polygon + token[1] + " " + token[2];
						firstx = token[1];
						firsty = token[2];
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
						first = false;
					}
					polygon = polygon + "," + token[1] + " " + token[2];
				}else{
					polygon = polygon + "," + firstx + " " + firsty + "))";
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
					pst.setString(15, polygon);
					pst.executeUpdate();
					
					firstx = token[1];
					firsty = token[2];
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
					
					id = newid;
					polygon = "POLYGON((" + token[1] + " " + token[2];
				}
				
				j++;
			}

			polygon = polygon + "," + firstx + " " + firsty + "))";
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
			pst.setString(15, polygon);
			pst.executeUpdate();
			
			System.out.print("World: " + j + " Lines eingelesen!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importConstituencies() throws Exception{
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
						polygon = polygon + token[0] + " " + token[1];
						firstx = token[0];
						firsty = token[1];
						partid = token[3];
						perimeter = token[4];
						wkr_nr = token[5];
						wkr_name = token[6];
						land_nr = token[7];
						land_name = token[8];
						flag = token[9];
						first = false;
					}
					polygon = polygon + "," + token[0] + " " + token[1];
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
					
					firstx = token[0];
					firsty = token[1];
					partid = token[3];
					perimeter = token[4];
					wkr_nr = token[5];
					wkr_name = token[6];
					land_nr = token[7];
					land_name = token[8];
					flag = token[9];
					
					id = newid;
					polygon = "POLYGON((" + token[0] + " " + token[1];
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

	public void importResults() throws Exception{
		File file = new File("kerg.csv");
		BufferedReader bufRdr = null;

		try {
			bufRdr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));}
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		db.executeUpdate("drop table if exists results");
		db.executeUpdate("drop table if exists parties");
		db.executeUpdate("drop table if exists voter");
		
		db.executeUpdate("CREATE TABLE parties ("
				+ " id int PRIMARY KEY,"
				+ "	name varchar)");

		Connection conn = db.getConn();
		PreparedStatement pstParties = conn.prepareStatement("INSERT INTO parties (id, name) VALUES (?,?)"); 

		db.executeUpdate("CREATE TABLE voter ("
				+ " id int PRIMARY KEY,"
				+ "	name varchar,"
				+ " part_of int," 
				+ "	elective_cur int," 
				+ "	elective_prev int,"
				+ "	voter_cur int," 
				+ "	voter_prev int)");

		PreparedStatement pstVoter = conn.prepareStatement("INSERT INTO voter (id, name, part_of, elective_cur, elective_prev, voter_cur, voter_prev) VALUES (?,?,?,?,?,?,?)"); 
		
		db.executeUpdate("CREATE TABLE results ("
				+ " constituencies_id int,"
				+ "	partie_id int," 
				+ " first_cur int," 
				+ "	first_prev int,"
				+ "	second_cur int," 
				+ "	second_prev int,"
				+ " FOREIGN KEY (constituencies_id) REFERENCES voter (id)," 
				+ " FOREIGN KEY (partie_id) REFERENCES parties (id))");
		
		PreparedStatement pstResult = conn.prepareStatement("INSERT INTO results (constituencies_id, partie_id, first_cur, first_prev, second_cur, second_prev) VALUES (?,?,?,?,?,?)");
		
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
			
			String constituencieID;
			
			while((line = bufRdr.readLine()) != null){
				token = line.split(";");
				
				if (token[0].equals("")) { }else {
					// Macht die Nummerierungen Primary-Key-Fähig.
					if ((Integer.parseInt(token[2])) == 99) {
						constituencieID = "99" + token[0];
					} else {
						constituencieID = token[0];
					}
					pstVoter.setInt(1, Integer.parseInt(constituencieID));
					pstVoter.setString(2, token[1]);
					pstVoter.setInt(3, Integer.parseInt(token[2]));
					pstVoter.setInt(4, Integer.parseInt(token[3]));
					pstVoter.setInt(5, Integer.parseInt(token[4]));
					pstVoter.setInt(6, Integer.parseInt(token[7]));
					pstVoter.setInt(7, Integer.parseInt(token[8]));
					pstVoter.executeUpdate();
					partieID = 0;
					for (int i = 11; i < token.length-1; i = i + 4) {

						pstResult.setInt(1, Integer.parseInt(constituencieID));
						pstResult.setInt(2, partieID);
						if (token[i].equals("")) {
							pstResult.setInt(3, 0);
						} else {
							pstResult.setInt(3, Integer.parseInt(token[i]));
						}

						if (token[i + 1].equals("")) {
							pstResult.setInt(4, 0);
						} else {
							pstResult.setInt(4, Integer.parseInt(token[i + 1]));
						}

						if (token[i + 2].equals("")) {
							pstResult.setInt(5, 0);
						} else {
							pstResult.setInt(5, Integer.parseInt(token[i + 2]));
						}

						if (token[i + 3].equals("")) {
							pstResult.setInt(6, 0);
						} else {
							pstResult.setInt(6, Integer.parseInt(token[i + 3]));
						}

						pstResult.executeUpdate();

						partieID++;
						}
				}
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ImportTool it = new ImportTool();
		// it.importStorcks();
		// it.importWorld();
		//it.importConstituencies();
		//it.importStorks();
		//it.importMalte();
		it.importResults();
	}

}
