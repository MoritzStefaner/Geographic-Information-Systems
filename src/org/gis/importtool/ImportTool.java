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

import org.gis.db.*;
import org.postgis.PGgeometry;

public class ImportTool {
	private Database db = null;

	/**
	 * @param args
	 */

	public ImportTool() {
		this.db = new Database();
	}

	public void importStorcks() {
		this.db.executeQuery("CREATE TABLE storcks ("
				+ "	id bigint PRIMARY KEY," + "	timestamp time,"
				+ "	locationLat double precision,"
				+ "	locationLong double precision," + "	altitude bigint,"
				+ "	tagLocalIdentifier bigint)");

		String insert = new String(
				"INSERT INTO storcks (id, timestamp, locationLat, locationLong, altitude, tagLocalIdentifier) VALUES ");
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
				newInsert = new String("(" + String.valueOf(i) + ", '"
						+ token[0] + "', " + token[2] + ", " + token[1] + ", "
						+ token[4] + ", " + token[25].replace("\"", "") + ")");

				insert = insert.concat(newInsert);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert = insert.concat(";");

		// System.out.println(insert);
		this.db.executeQuery(insert);

	}

	public void importWorld() throws Exception {
		db.executeQuery("drop table if exists world");
		this.db.executeQuery("CREATE TABLE world (" 
				+ "id int," 
				+ "x_coordinate double precision,"
				+ "	y_coordinate double precision," 
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

		Connection conn = db.getConn();
		conn.setAutoCommit(false);
		PreparedStatement pst = conn.prepareStatement("INSERT INTO world (id, x_coordinate, y_coordinate, z, m, fips, iso2, iso3, un, name, area, pop2005, region, subregion, lon, lat) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	
		
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
		
		int i = 0;
		int j = 0;

		// read each line of text file
		try {
			bufRdr.readLine();
			while ((line = bufRdr.readLine()) != null) {
				token = line.split("\t");
				
				pst.setInt(1, Integer.parseInt(token[0]));
				pst.setDouble(2, Double.parseDouble(token[1]));
				pst.setDouble(3, Double.parseDouble(token[2]));
				pst.setInt(4, Integer.parseInt(token[3]));
				pst.setInt(5, Integer.parseInt(token[4]));
				pst.setString(6, token[5]);
				pst.setString(7, token[6]);
				pst.setString(8, token[7]);
				pst.setInt(9, Integer.parseInt(token[8]));
				pst.setString(10, token[9]);
				pst.setInt(11, Integer.parseInt(token[10]));
				pst.setInt(12, Integer.parseInt(token[11]));
				pst.setInt(13, Integer.parseInt(token[12]));
				pst.setInt(14, Integer.parseInt(token[13]));
				pst.setDouble(15, Double.parseDouble(token[14]));
				pst.setDouble(16, Double.parseDouble(token[15]));
				pst.executeUpdate();
				
				i++;
				j++;
								
				if(i > 1000){
					i = 0;
					conn.commit();
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
			System.out.print(j++ + " Lines eingelesen!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ImportTool it = new ImportTool();
		// it.importStorcks();
		it.importWorld();
	}

}
