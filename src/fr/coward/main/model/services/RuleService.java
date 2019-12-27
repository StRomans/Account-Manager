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
import fr.coward.main.model.Category;
import fr.coward.main.model.Rule;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.rule.RuleOperator;

public class RuleService {
	
	public static final String TABLE = "`RULE`";
	
	public static final String COLUMN_ID = "`ID`";
	public static final String COLUMN_PRIORITY = "`PRIORITY`";
	public static final String COLUMN_OPERATOR = "`OPERATOR`";
	public static final String COLUMN_VALIDATORS = "`VALIDATORS`";
	public static final String COLUMN_SUB_CATEGORY_ID = "`SUB_CATEGORY_ID`";
	
	
	public static List<Rule> findAll(){
		
		List<Rule> ruleList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT " 
					+ "ru." + COLUMN_ID + "," 
					+ "ru." + COLUMN_PRIORITY + ","
					+ "ru." + COLUMN_OPERATOR + "," 
					+ "ru." + COLUMN_VALIDATORS + ","
					+ "ru." + COLUMN_SUB_CATEGORY_ID + "," 
					+ "subCat." + SubCategoryService.COLUMN_LABEL + ","
					+ "cat." + CategoryService.COLUMN_LABEL
					+ " FROM " + TABLE + " ru "
					+ " INNER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=ru." + COLUMN_SUB_CATEGORY_ID
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " ORDER BY ru." + COLUMN_PRIORITY + " ASC;";
			statement = connection.prepareStatement(selectQuery);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int ruleId = rs.getInt(1);
				int rulePriority = rs.getInt(2);
				RuleOperator operator = RuleOperator.valueOf(rs.getString(3));
				SubCategory subCategory = new SubCategory(rs.getString(6));
				subCategory.setId(rs.getInt(5));
				subCategory.setCategory(new Category(rs.getString(7)));
				
				Rule rule = new Rule();
				rule.setId(ruleId);
				rule.setPriority(rulePriority);
				rule.setIntraOperator(operator);
				rule.setSubCategory(subCategory);
				rule.deserializeValidators(rs.getString(4));
				
				ruleList.add(rule);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return ruleList;
	}
	
	public static List<Rule> findAllBySubCategory(int subCategoryId){
		
		List<Rule> ruleList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT " 
					+ "ru." + COLUMN_ID + "," 
					+ "ru." + COLUMN_PRIORITY + ","
					+ "ru." + COLUMN_OPERATOR + "," 
					+ "ru." + COLUMN_VALIDATORS + ","
					+ "ru." + COLUMN_SUB_CATEGORY_ID + "," 
					+ "subCat." + SubCategoryService.COLUMN_LABEL + ","
					+ "cat." + CategoryService.COLUMN_LABEL
					+ " FROM " + TABLE + " ru "
					+ " INNER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=ru." + COLUMN_SUB_CATEGORY_ID
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " WHERE subCat." + SubCategoryService.COLUMN_ID + " = ? "
					+ " ORDER BY ru." + COLUMN_PRIORITY + " ASC;";
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, subCategoryId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int ruleId = rs.getInt(1);
				int rulePriority = rs.getInt(2);
				RuleOperator operator = RuleOperator.valueOf(rs.getString(3));
				SubCategory subCategory = new SubCategory(rs.getString(6));
				subCategory.setId(rs.getInt(5));
				subCategory.setCategory(new Category(rs.getString(7)));
				
				Rule rule = new Rule();
				rule.setId(ruleId);
				rule.setPriority(rulePriority);
				rule.setIntraOperator(operator);
				rule.setSubCategory(subCategory);
				rule.deserializeValidators(rs.getString(4));
				
				ruleList.add(rule);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return ruleList;
	}

	/**
	 * Sauvegarde l'{@link Rule}
	 * @param rule
	 */
	public static void save(Rule rule){
		
		if(null != rule){
			
			if(rule.getId() == 0){
				insert(rule);
			} else {
				update(rule);
			}
		}
	}
	
	private static void update(Rule rule){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " 
					+ COLUMN_PRIORITY + "=?,"
					+ COLUMN_OPERATOR + "=?,"
					+ COLUMN_SUB_CATEGORY_ID + "=?,"
					+ COLUMN_VALIDATORS + "=?"
					+ " WHERE " + COLUMN_ID + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setInt(index++, rule.getPriority());
			statement.setString(index++, rule.getIntraOperator().name());
			statement.setInt(index++, rule.getSubCategory().getId());
			statement.setString(index++, rule.serializeValidators());
			statement.setInt(index++, rule.getId());
			
			statement.executeUpdate();
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			DbUtil.closeStatement(statement);
		}
	}
	
	private static void insert(Rule rule){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String insertQuery = "INSERT INTO " + TABLE + " (" + COLUMN_PRIORITY + "," + COLUMN_OPERATOR + "," + COLUMN_SUB_CATEGORY_ID + "," + COLUMN_VALIDATORS + ") "
					+ " SELECT ?,?,?,? ";
			statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			int index=1;
			statement.setInt(index++, rule.getPriority());
			statement.setString(index++, rule.getIntraOperator().name());
			statement.setInt(index++, rule.getSubCategory().getId());
			statement.setString(index++, rule.serializeValidators());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	rule.setId(generatedKeys.getInt(1));
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
	}

	/**
	 * Supprime la {@link Rule}
	 * @param rule
	 */
	public static void delete(Rule rule){
		
		if(null != rule && rule.getId() != 0){
			
			Connection connection = SqLiteConnection.getInstance().getConnection();
			PreparedStatement statement = null;
			
			try{
			
				String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + COLUMN_ID + "=?";
				statement = connection.prepareStatement(deleteQuery);
				statement.setInt(1, rule.getId());
				
				statement.execute();
				
			}catch(Exception e){
				System.err.println(e.getMessage());
			} finally {
				DbUtil.closeStatement(statement);
			}
		}
	}
}
