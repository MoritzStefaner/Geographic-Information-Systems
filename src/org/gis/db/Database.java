package org.gis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.postgis.*;
import org.postgresql.PGConnection;;

public class Database {
	private Connection conn = null;
	private final String url = "jdbc:postgresql://localhost:5432/gis"; //connection string using localhost and postgis database
	
	public Database() {
		try {
			Class.forName("org.postgresql.Driver"); //create postgresql driver
			
			this.conn = DriverManager.getConnection(this.url,"postgres","postgres"); //connect to database with username=postgres
			((PGConnection) this.conn).addDataType("geometry", org.postgis.PGgeometry.class); //add support for Geometry types
		} catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String query) {
		ResultSet r = null;
		
		try {
			Statement s = this.conn.createStatement(); //create query statement
			r = s.executeQuery(query); //get all polygons from polytest table
			
			s.close(); //close statement when finished
		} catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}

		return r;
	}
	
	protected void finalize() throws Throwable {
		if (this.conn != null) {
			try {
				this.conn.close(); //close connection when finished
			} catch(Exception ex) {}
		}
		
		super.finalize(); 
	}

}
