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
import fr.coward.main.model.SubCategory;

public class CategoryService {
	
	public static final String TABLE = "`CATEGORY`";
	
	public static final String COLUMN_ID = "`ID`";
	public static final String COLUMN_LABEL = "`LABEL`";
	public static final String COLUMN_COLOR = "`COLOR`";
	
	public static Category findbyId(int categoryId){
		
		Category category = null;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_LABEL + "," + COLUMN_COLOR
					+ " FROM " + TABLE 
					+ " WHERE " + COLUMN_ID + " =?";
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, categoryId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				category = new Category();
				category.setId(rs.getInt(1));
				category.setLabel(rs.getString(2));
				category.setColor(rs.getString(3));
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return category;
	}

	public static List<Category> findAll(){
		
		List<Category> categoryList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_LABEL + "," + COLUMN_COLOR
					+ " FROM " + TABLE 
					+ " ORDER BY " + COLUMN_LABEL + " ASC;";
			statement = connection.prepareStatement(selectQuery);

			rs = statement.executeQuery();
			
			Category category = null;
			
			while(rs.next()){
				
				category = new Category();
				category.setId(rs.getInt(1));
				category.setLabel(rs.getString(2));
				category.setColor(rs.getString(3));
				
				categoryList.add(category);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return categoryList;
	}
	
	/**
	 * Sauvegarde l'{@link Category}
	 * @param category
	 */
	public static boolean save(Category category){
		
		if(null != category){
			
			if(category.getId() == 0){
				return insert(category);
			} else {
				return update(category);
			}
		}
		
		return false;
	}
	
	private static boolean insert(Category category){
		
		boolean success = false;
	
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String insertQuery = "INSERT INTO " + TABLE + " (" + COLUMN_LABEL + "," + COLUMN_COLOR + ") SELECT ?,? "
					+ " WHERE NOT EXISTS (SELECT 0 FROM " + TABLE + " WHERE " + COLUMN_LABEL +"=?)";
			statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			int index=1;
			statement.setString(index++, category.getLabel());
			statement.setString(index++, category.getColor());
			statement.setString(index++, category.getLabel());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	int categoryId = generatedKeys.getInt(1);
	            	category.setId(categoryId);
	            	
	            	success = categoryId != 0; 
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
	
	private static boolean update(Category category){
		
		boolean success = false;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + COLUMN_LABEL + "=?," + COLUMN_COLOR + "=? WHERE " + COLUMN_ID + "=? AND NOT EXISTS (SELECT 0 FROM " + TABLE + " WHERE " + COLUMN_LABEL + "= ?)";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setString(index++, category.getLabel());
			statement.setString(index++, category.getColor());
			statement.setInt(index++, category.getId());
			statement.setString(index++, category.getLabel());
			
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
	
	/**
	 * Supprime la {@link Category}
	 * @param category
	 */
	public static void delete(Category category){
		
		if(null != category && category.getId() != 0){
			
			Connection connection = SqLiteConnection.getInstance().getConnection();
			PreparedStatement statement = null;
			
			try{
				
				List<SubCategory> listLinkedSubCategory = SubCategoryService.findAllByCategory(category.getId());
				if(null != listLinkedSubCategory && !listLinkedSubCategory.isEmpty()){
					
					for(SubCategory subCategoryToDelete : listLinkedSubCategory){
						SubCategoryService.delete(subCategoryToDelete);
					}					
				}
			
				String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + COLUMN_ID + "=?";
				statement = connection.prepareStatement(deleteQuery);
				statement.setInt(1, category.getId());
				
				statement.execute();
				
			}catch(Exception e){
				System.err.println(e.getMessage());
			} finally {
				DbUtil.closeStatement(statement);
			}
		}
	}
}
