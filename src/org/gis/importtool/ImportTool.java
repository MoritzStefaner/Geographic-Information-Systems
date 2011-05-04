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
		
		//read each line of text file
		try {
			bufRdr.readLine();
			while((line = bufRdr.readLine()) != null) {	
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
		
		//System.out.println(insert);
		this.db.executeQuery(insert);

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
		} catch (FileNotFoundException e1) {
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImportTool it = new ImportTool();
		it.importStorks();
		//it.importMalte();
	}

}
