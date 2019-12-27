package fr.coward.main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqLiteConnection {
	
	private static final String JDBC_SQLITE_DRIVER = "org.sqlite.JDBC";
	private static final String DATABASE_URL = "jdbc:sqlite:accountManager.db";
	
	private static SqLiteConnection instance = null;
	private Connection connection;
	
	/**
	 * 
	 */
	private SqLiteConnection(){
		connection = null;
	}
	
	/**
	 * @return
	 */
	public static synchronized SqLiteConnection getInstance(){
		
		if(null == instance){
			instance = new SqLiteConnection();
			
			// Mettre la base de donnée à niveau
			MigrationManager.migrate(DATABASE_URL);
		}
		
		return instance;
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection() {

	    
	    try{
			Class.forName(JDBC_SQLITE_DRIVER);
			connection = DriverManager.getConnection(DATABASE_URL);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	    
		return connection;
	}
	
	public void close(){
		try {
			this.getConnection().commit();
			this.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
