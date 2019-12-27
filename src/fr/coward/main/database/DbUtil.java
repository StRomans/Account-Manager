package fr.coward.main.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {

	public static void closeStatement(Statement statement){

		if(null != statement){
			try {
				statement.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet resultSet){

		if(null != resultSet){
			try {
				resultSet.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static String addToWhereClause(String whereClause, String newClause){
		if(null != whereClause && !whereClause.isEmpty()) whereClause += " AND ";
		
		return whereClause + newClause;
	}
}
