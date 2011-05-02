package org.gis.importtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		this.db.executeQuery(
				"CREATE TABLE storcks (" +
				"	id bigint PRIMARY KEY," +
				"	timestamp time," +
				"	locationLat double precision," +
				"	locationLong double precision," +
				"	altitude bigint," +
				"	tagLocalIdentifier text)"
				);
		
		String insert = new String("INSERT INTO storcks (id, timestamp, locationLat, locationLong, altitude, tagLocalIdentifier) VALUES ");
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
			bufRdr.readLine();
			while((line = bufRdr.readLine()) != null && i < 10) {	
				if (i != 0) {
					insert = insert.concat(",");
				}
				token = line.split(",");
				newInsert = new String("(" + String.valueOf(i) + ", '" + token[0] + "', " + token[2] + ", " + token[1] + ", " + token[4] + ", " + token[25] + ")");
				
				insert = insert.concat(newInsert);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert = insert.concat(";");
		
		//System.out.println(insert);
		this.db.executeQuery(insert);

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImportTool it = new ImportTool();
		it.importStorcks();
	}

}
