package fr.coward.main.model.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import fr.coward.main.database.DbUtil;
import fr.coward.main.database.SqLiteConnection;
import fr.coward.main.model.Account;

public class AccountService {
	
	public static final String TABLE = "`ACCOUNT`";
	
	public static final String COLUMN_ID = "`ID`";
	public static final String COLUMN_LABEL = "`LABEL`";
	public static final String COLUMN_CURRENCY = "`CURRENCY`";
	
	public static Account findById(int accountId){
		
		Account account = null;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_LABEL + "," + COLUMN_CURRENCY + " FROM " + TABLE + " WHERE " + COLUMN_ID + " =?;";
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, accountId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				String label = rs.getString(2);
				String currency = rs.getString(3);
				
				account = new Account(label, currency);
				account.setId(id);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return account;
	}

	public static List<Account> findAll(){
		
		List<Account> accountList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String updateQuery = "SELECT " + COLUMN_ID + "," + COLUMN_LABEL + "," + COLUMN_CURRENCY + " FROM " + TABLE + " ORDER BY " + COLUMN_LABEL + " ASC;";
			statement = connection.prepareStatement(updateQuery);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				String label = rs.getString(2);
				String currency = rs.getString(3);
				
				Account account = new Account(label, currency);
				account.setId(id);
				
				accountList.add(account);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return accountList;
	}
	
	/**
	 * Sauvegarde l'{@link Account}
	 * @param account
	 * @return 
	 */
	public static boolean save(Account account){
		
		if(null != account){
			
			if(account.getId() == 0){
				return insert(account);
			} else {
				return update(account);
			}
		}
		
		return false;
	}
	
	private static boolean insert(Account account){
		
		boolean success = false;
	
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String insertQuery = "INSERT INTO " + TABLE + " (" + COLUMN_LABEL + "," + COLUMN_CURRENCY + ") SELECT ?,? WHERE NOT EXISTS (SELECT 0 FROM " + TABLE + " WHERE LABEL=?)";
			statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			statement.setString(1, account.getLabel());
			statement.setString(2, account.getCurrency());
			statement.setString(3, account.getLabel());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	account.setId(generatedKeys.getInt(1));
	            	
	            	success = account.getId() != 0; 
	            }
	            else {
	                throw new SQLException("Creating account failed, no ID obtained.");
	            }
	        }
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			DbUtil.closeStatement(statement);
		}
		
		return success;
	}
	
	private static boolean update(Account account){
		
		boolean success = false;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + COLUMN_LABEL + "=?," + COLUMN_CURRENCY + "=? WHERE " + COLUMN_ID + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			statement.setString(1, account.getLabel());
			statement.setString(2, account.getCurrency());
			statement.setInt(3, account.getId());
			
			success = statement.executeUpdate() != 0;
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			DbUtil.closeStatement(statement);
		}
		
		return success;
	}
}
