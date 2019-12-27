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
import fr.coward.main.model.Rule;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;

public class SubCategoryService {
	
	public static final String TABLE = "`SUB_CATEGORY`";
	
	public static final String COLUMN_ID = "`ID`";
	public static final String COLUMN_LABEL = "`LABEL`";
	public static final String COLUMN_CATEGORY_ID = "`CATEGORY_ID`";
	
	public static SubCategory findbyId(int subCategoryId){
		
		SubCategory subCategory = null;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			String selectQuery = "SELECT sbCat." + COLUMN_ID + ",sbCat." + COLUMN_LABEL + ",sbCat." + COLUMN_CATEGORY_ID
					+ " FROM " + TABLE + " sbCat "
					+ " WHERE " + COLUMN_ID + " =?";;
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, subCategoryId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				subCategory = new SubCategory();
				subCategory.setId(rs.getInt(1));
				subCategory.setLabel(rs.getString(2));
				subCategory.setCategory(CategoryService.findbyId(rs.getInt(3)));
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return subCategory;
	}

	public static List<SubCategory> findAll(){
		
		List<SubCategory> subCategoryList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT sbCat." + COLUMN_ID + ",sbCat." + COLUMN_LABEL + ",sbCat." + COLUMN_CATEGORY_ID
					+ " FROM " + TABLE + " sbCat "
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=sbCat." + COLUMN_CATEGORY_ID
					+ " ORDER BY cat." + CategoryService.COLUMN_LABEL + "ASC, sbCat." + COLUMN_LABEL + " ASC";
			statement = connection.prepareStatement(selectQuery);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				String label = rs.getString(2);
				int categoryId = rs.getInt(3);
				
				SubCategory subCategory = new SubCategory();
				subCategory.setId(id);
				subCategory.setLabel(label);
				subCategory.setCategory(CategoryService.findbyId(categoryId));
				
				subCategoryList.add(subCategory);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return subCategoryList;
	}
	
	public static List<SubCategory> findAllByCategory(int categoryId){
		
		List<SubCategory> subCategoryList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT sbCat." + COLUMN_ID + ",sbCat." + COLUMN_LABEL
					+ " FROM " + TABLE + " sbCat "
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=sbCat." + COLUMN_CATEGORY_ID
					+ " WHERE sbCat." + COLUMN_CATEGORY_ID + "= ? "
					+ " ORDER BY cat." + CategoryService.COLUMN_LABEL + "ASC, sbCat." + COLUMN_LABEL + " ASC";
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, categoryId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt(1);
				String label = rs.getString(2);
				
				SubCategory subCategory = new SubCategory();
				subCategory.setId(id);
				subCategory.setLabel(label);
				subCategory.setCategory(CategoryService.findbyId(categoryId));
				
				subCategoryList.add(subCategory);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return subCategoryList;
	}
	
	/**
	 * Sauvegarde l'{@link SubCategory}
	 * @param subCategory
	 */
	public static boolean save(SubCategory subCategory){
		
		if(null != subCategory){
			
			if(subCategory.getId() == 0){
				return insert(subCategory);
			} else {
				return update(subCategory);
			}
		}
		
		return false;
	}
	
	private static boolean insert(SubCategory subCategory){
		
		boolean success = false;
	
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String insertQuery = "INSERT INTO " + TABLE + " (" + COLUMN_LABEL + "," + COLUMN_CATEGORY_ID + ") SELECT ?,? "
					+ " WHERE NOT EXISTS (SELECT 0 FROM " + TABLE + " WHERE " + COLUMN_LABEL +"=? AND " + COLUMN_CATEGORY_ID +  "=?)";
			statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			int index=1;
			statement.setString(index++, subCategory.getLabel());
			statement.setInt(index++, subCategory.getCategory().getId());
			statement.setString(index++, subCategory.getLabel());
			statement.setInt(index++, subCategory.getCategory().getId());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	subCategory.setId(generatedKeys.getInt(1));
	            	
	            	success = subCategory.getId() != 0;
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
	
	private static boolean update(SubCategory subCategory){
		
		boolean success = false;
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + COLUMN_LABEL + "=?, " + COLUMN_CATEGORY_ID + "=?  WHERE " + COLUMN_ID + "=?"
					+ " AND NOT EXISTS (SELECT 0 FROM " + TABLE + " WHERE " + COLUMN_CATEGORY_ID + "=? AND " + COLUMN_LABEL + "=?)";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setString(index++, subCategory.getLabel());
			statement.setInt(index++, subCategory.getCategory().getId());
			statement.setInt(index++, subCategory.getId());
			statement.setInt(index++, subCategory.getCategory().getId());
			statement.setString(index++, subCategory.getLabel());
			
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
	 * Supprime la {@link SubCategory}
	 * @param subCategory
	 */
	public static void delete(SubCategory subCategory){
		
		if(null != subCategory && subCategory.getId() != 0){
			
			Connection connection = SqLiteConnection.getInstance().getConnection();
			PreparedStatement statement = null;
			
			try{
			
				String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + COLUMN_ID + "=?";
				statement = connection.prepareStatement(deleteQuery);
				statement.setInt(1, subCategory.getId());
				
				statement.execute();
				
				// Remettre les transactions placées sous cette catégorie en "à classer"
				List<Transaction> listTransactionToUnclassify = TransactionService.findAllBySubCategory(subCategory.getId());
				if(null != listTransactionToUnclassify && !listTransactionToUnclassify.isEmpty()){
					
					for(Transaction transactionToUnclassify : listTransactionToUnclassify){
						TransactionService.unclassify(transactionToUnclassify.getId());
					}					
				}
				// Supprimer les règles qui sont devenues obsolètes
				List<Rule> listObsoleteRules = RuleService.findAllBySubCategory(subCategory.getId());
				if(null != listObsoleteRules && !listObsoleteRules.isEmpty()){
					
					for(Rule ruleToDelete : listObsoleteRules){
						RuleService.delete(ruleToDelete);
					}					
				}
				
			}catch(Exception e){
				System.err.println(e.getMessage());
			} finally {
				DbUtil.closeStatement(statement);
			}
		}
	}
}
