package org.gis.importtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

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
//		this.db.executeQuery(
//				"CREATE TABLE storcks (" +
//				"	id int PRIMARY KEY," +
//				"	timestamp time," +
//				"	location-lat double," +
//				"	location-long double," +
//				"	altitude int," +
//				"	tag-local-identifier int"
//				);
		
		String insert = new String("INSERT INTO storcks (id, timestamp, location-lat, location-long, altitude, tag-local-identifier) VALUES ");
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
		
		//read each line of text file
		try {
			while((line = bufRdr.readLine()) != null) {	
				token = line.split(",");
				newInsert = new String("(" + String.valueOf(i) + ", " + token[0] + ", " + token[2] + ", " + token[1] + ", " + token[4] + ", " + token[25] + "), ");
				
				insert = insert.concat(newInsert);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(insert);
		//this.db.executeQuery(insert);

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImportTool it = new ImportTool();
		it.importStorcks();
	}

}
