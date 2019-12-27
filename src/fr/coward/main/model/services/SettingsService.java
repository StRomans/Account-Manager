package fr.coward.main.model.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fr.coward.main.database.DbUtil;
import fr.coward.main.database.SqLiteConnection;
import fr.coward.main.model.Settings;

public class SettingsService {
	
	public static final String TABLE = "`SETTINGS`";
	
	public static final String COLUMN_CODE = "`CODE`";
	public static final String COLUMN_VALUE = "`VALUE`";
	
	public enum Option {
		THEME;
	}
	
	public static Settings findByCode(Option settingsCode){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		Settings returnedSetting = new Settings(settingsCode.name());
		
		try{
			String selectQuery = "SELECT " + COLUMN_VALUE + " FROM " + TABLE + " WHERE " + COLUMN_CODE + " = ?";
			
			statement = connection.prepareStatement(selectQuery);
			
			statement.setString(1, settingsCode.name());
			
			rs = statement.executeQuery();
			
			if(rs.next()){
				returnedSetting.setValue(rs.getString(1));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}
		
		return returnedSetting;
	}
	
	public static void update(Settings setting){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + 
											COLUMN_VALUE + "=? " + 
								" WHERE " + COLUMN_CODE + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setString(index++, setting.getValue());
			statement.setString(index++, setting.getCode());
			
			
			statement.executeUpdate();
		} 
		catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			DbUtil.closeStatement(statement);
		}
	}
}
