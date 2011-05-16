package org.gis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
	
	public void executeUpdate(String query) {
		try {
			Statement s = this.conn.createStatement(); //create query statement
			s.executeUpdate(query);
			
			s.close(); //close statement when finished
		} catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}

	}
	
	public  ResultSet executeQuery(String query){
		ResultSet result = null;
		try {
			Statement s = this.conn.createStatement(); //create query statement
			 result = s.executeQuery(query);
			
			//s.close(); //close statement when finished
		} catch(Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public Connection getConn(){
		return conn;
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
