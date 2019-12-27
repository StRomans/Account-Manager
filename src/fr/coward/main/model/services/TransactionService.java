package fr.coward.main.model.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.coward.main.database.DbUtil;
import fr.coward.main.database.SqLiteConnection;
import fr.coward.main.model.Account;
import fr.coward.main.model.Category;
import fr.coward.main.model.Session;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.DTO.TransactionViewDTO;
import fr.coward.main.model.utils.Period;
import fr.coward.main.ui.utils.StringUtil;

public class TransactionService {
	
	public static final String TABLE = "`TRANSACTION`";
	
	public static final String COLUMN_ID = "`ID`";
	public static final String COLUMN_DATE = "`DATE`";
	public static final String COLUMN_AMOUNT = "`AMOUNT`";
	public static final String COLUMN_LABEL = "`LABEL`";
	public static final String COLUMN_IDENTIFIER = "`IDENTIFIER`";
	public static final String COLUMN_ACCOUNT_ID = "`ACCOUNT_ID`";
	public static final String COLUMN_SUB_CATEGORY_ID = "`SUB_CATEGORY_ID`";
	
	public static List<TransactionViewDTO> findAllDTOByComplexCriteria(Account account, Date startDate, Date endDate, String label, Category category, SubCategory subCategory){
		
		List<TransactionViewDTO> transactionList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String whereClause = "";
			
			if (null != account) {
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_ACCOUNT_ID + "=?");
			}
			if(null != startDate && null != endDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " between ? and ?" );
			}
			else if(null != startDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " >= ?" );
			}
			else if(null != endDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " <= ?" );
			}
			if(null != label && !label.isEmpty()){
				whereClause = DbUtil.addToWhereClause(whereClause, "UPPER(tr." + COLUMN_LABEL + ") like UPPER(?)" );
			}
			if(null != category){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_SUB_CATEGORY_ID + " in (SELECT subCat." + SubCategoryService.COLUMN_ID + " FROM " + SubCategoryService.TABLE + " subCat WHERE subCat." + SubCategoryService.COLUMN_CATEGORY_ID + " = ?)" );
			}
			if(null != subCategory){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_SUB_CATEGORY_ID + "=?");
			}
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 							// 1
					+ ",tr." + COLUMN_DATE 							// 2
					+ ",tr." + COLUMN_AMOUNT 						// 3
					+ ",tr." + COLUMN_LABEL 						// 4
					+ ",subCat." + SubCategoryService.COLUMN_ID		// 5
					+ ",subCat." + SubCategoryService.COLUMN_LABEL	// 6
					+ ",cat." + CategoryService.COLUMN_ID			// 7
					+ ",cat." + CategoryService.COLUMN_LABEL		// 8
					+ ",acc." + AccountService.COLUMN_ID			// 9
					+ ",acc." + AccountService.COLUMN_LABEL			// 10
					+ ",acc." + AccountService.COLUMN_CURRENCY		// 11
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " LEFT OUTER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=tr." + COLUMN_SUB_CATEGORY_ID
					+ " LEFT OUTER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " WHERE " + whereClause
					+ " ORDER BY tr." + COLUMN_ACCOUNT_ID + "ASC"
							+ ",tr." + COLUMN_DATE + "DESC"
							+ ",tr." + COLUMN_LABEL + " ASC"
							+ ",tr." + COLUMN_AMOUNT + " DESC";
			
			statement = connection.prepareStatement(selectQuery);
			
			int paramCount = 1;
			if (null != account) {
				statement.setInt(paramCount++, account.getId());
			}
			if(null != startDate){
				statement.setString(paramCount++, StringUtil.getFormattedDate(startDate, StringUtil.DB_DATE_PATTERN));
			}
			if(null != endDate){
				statement.setString(paramCount++, StringUtil.getFormattedDate(endDate, StringUtil.DB_DATE_PATTERN));
			}
			if(null != label && !label.isEmpty()){
				statement.setString(paramCount++, '%' + label + '%');
			}
			if(null != category){
				statement.setInt(paramCount++, category.getId());
			}
			if(null != subCategory){
				statement.setInt(paramCount++, subCategory.getId());
			}
	
			rs = statement.executeQuery();
			
			while(rs.next()){
				
				TransactionViewDTO transactionDto = new TransactionViewDTO();
				transactionDto.setId(rs.getInt(1));
				transactionDto.setDate(StringUtil.toDate(rs.getString(2)));
				transactionDto.setLabel(rs.getString(4));
				transactionDto.setAmount(rs.getDouble(3));
				
				Category l_category = new Category(rs.getInt(7), rs.getString(8));
				transactionDto.setCategory(l_category);
				
				SubCategory l_subCategory = new SubCategory(rs.getInt(5), rs.getString(6), l_category);
				transactionDto.setSubCategory(l_subCategory);
				
				Account l_account = new Account(rs.getInt(9), rs.getString(10), rs.getString(11));
				transactionDto.setAccount(l_account);
				
				transactionList.add(transactionDto);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}		
		
		return transactionList;
	}
	
	public static List<Transaction> findAllByComplexCriteria(Account account, Date startDate, Date endDate, String label, Category category, SubCategory subCategory){
		
		List<Transaction> transactionList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String whereClause = "";
			
			if (null != account) {
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_ACCOUNT_ID + "=?");
			}
			if(null != startDate && null != endDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " between ? and ?" );
			}
			else if(null != startDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " >= ?" );
			}
			else if(null != endDate){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_DATE + " <= ?" );
			}
			if(null != label && !label.isEmpty()){
				whereClause = DbUtil.addToWhereClause(whereClause, "UPPER(tr." + COLUMN_LABEL + ") like UPPER(?)" );
			}
			if(null != category){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_SUB_CATEGORY_ID + " in (SELECT subCat." + SubCategoryService.COLUMN_ID + " FROM " + SubCategoryService.TABLE + " subCat WHERE subCat." + SubCategoryService.COLUMN_CATEGORY_ID + " = ?)" );
			}
			if(null != subCategory){
				whereClause = DbUtil.addToWhereClause(whereClause, "tr." + COLUMN_SUB_CATEGORY_ID + "=?");
			}
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 							// 1
					+ ",tr." + COLUMN_DATE 							// 2
					+ ",tr." + COLUMN_AMOUNT 						// 3
					+ ",tr." + COLUMN_LABEL 						// 4
					+ ",subCat." + SubCategoryService.COLUMN_ID		// 5
					+ ",subCat." + SubCategoryService.COLUMN_LABEL	// 6
					+ ",cat." + CategoryService.COLUMN_ID			// 7
					+ ",cat." + CategoryService.COLUMN_LABEL		// 8
					+ ",acc." + AccountService.COLUMN_ID			// 9
					+ ",acc." + AccountService.COLUMN_LABEL			// 10
					+ ",acc." + AccountService.COLUMN_CURRENCY		// 11
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " LEFT OUTER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=tr." + COLUMN_SUB_CATEGORY_ID
					+ " LEFT OUTER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " WHERE " + whereClause
					+ " ORDER BY tr." + COLUMN_ACCOUNT_ID + "ASC"
							+ ",tr." + COLUMN_DATE + "DESC"
							+ ",tr." + COLUMN_LABEL + " ASC"
							+ ",tr." + COLUMN_AMOUNT + " DESC";
			
			statement = connection.prepareStatement(selectQuery);
			
			int paramCount = 1;
			if (null != account) {
				statement.setInt(paramCount++, account.getId());
			}
			if(null != startDate){
				statement.setString(paramCount++, StringUtil.getFormattedDate(startDate, StringUtil.DB_DATE_PATTERN));
			}
			if(null != endDate){
				statement.setString(paramCount++, StringUtil.getFormattedDate(endDate, StringUtil.DB_DATE_PATTERN));
			}
			if(null != label && !label.isEmpty()){
				statement.setString(paramCount++, '%' + label + '%');
			}
			if(null != category){
				statement.setInt(paramCount++, category.getId());
			}
			if(null != subCategory){
				statement.setInt(paramCount++, subCategory.getId());
			}
	
			rs = statement.executeQuery();
			
			while(rs.next()){
				
				Transaction transaction = new Transaction();
				transaction.setId(rs.getInt(1));
				transaction.setDate(StringUtil.toDate(rs.getString(2)));
				transaction.setLabel(rs.getString(4));
				transaction.setAmount(rs.getDouble(3));
				
				Account l_account = new Account(rs.getString(10),rs.getString(11));
				l_account.setId(rs.getInt(9));
				transaction.setAccount(l_account);
				
				Category l_category = new Category(rs.getString(8));
				l_category.setId(rs.getInt(7));
				
				SubCategory l_subCategory = new SubCategory(rs.getString(6));
				l_subCategory.setId(rs.getInt(5));
				l_subCategory.setCategory(category);
				transaction.setSubCategory(l_subCategory);
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}		
		
		return transactionList;
	}
	
	public static Date findNewestTransactionDateByAccount(Account account){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		Statement statement = null;
		ResultSet rs = null;
		
		Date newestTransactionDate = null;
		
		try{
			String selectQuery = "SELECT max(" + COLUMN_DATE + ") FROM " + TABLE;
			
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			
			if(rs.next()){
				String dateStr = rs.getString(1);
				if(StringUtil.isNotNullNotEmpty(dateStr)) newestTransactionDate = StringUtil.toDate(dateStr);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}
		
		return newestTransactionDate;
	}
	
	public static List<Transaction> findAllSpendingsClassifiedByAccountForPeriod(Account account, Date startDate, Date endDate, Category categoryToMatch){
		
		List<Transaction> transactionList = new LinkedList<>();
		
		if(null == account){
			return transactionList;
		}
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String dateRestriction = (null != startDate && null != endDate)? " AND (tr." + COLUMN_DATE + " between ? and ? ) "  : "";
			String categoryRestriction = (null != categoryToMatch)? " AND (cat." + CategoryService.COLUMN_ID + " = ? ) "  : "";
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 
					+ ",tr." + COLUMN_DATE 
					+ ",tr." + COLUMN_AMOUNT 
					+ ",tr." + COLUMN_LABEL 
					+ ",subCat." + SubCategoryService.COLUMN_ID
					+ ",subCat." + SubCategoryService.COLUMN_LABEL
					+ ",cat." + CategoryService.COLUMN_ID
					+ ",cat." + CategoryService.COLUMN_LABEL
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " INNER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=tr." + COLUMN_SUB_CATEGORY_ID
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " WHERE tr." + COLUMN_SUB_CATEGORY_ID + " is not null "
					+ " AND tr." + COLUMN_AMOUNT + " < 0 "
					+ " AND acc." + AccountService.COLUMN_ID + "=?"
					+ dateRestriction
					+ categoryRestriction
					+ " ORDER BY cat." + CategoryService.COLUMN_LABEL + "ASC"
					+ ",subCat." + SubCategoryService.COLUMN_LABEL +" ASC"
					+ ",tr." + COLUMN_DATE + " DESC";
			
			statement = connection.prepareStatement(selectQuery);
			
			int index = 1;
			statement.setInt(index++, account.getId());
			if(!dateRestriction.isEmpty()){
				statement.setString(index++, StringUtil.getFormattedDate(startDate, StringUtil.DB_DATE_PATTERN));
				statement.setString(index++, StringUtil.getFormattedDate(endDate, StringUtil.DB_DATE_PATTERN));
			}
			
			if(!categoryRestriction.isEmpty()){
				statement.setInt(index++, categoryToMatch.getId());
			}

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int transactionId = rs.getInt(1);
				Date transactionDate = StringUtil.toDate(rs.getString(2));
				Double transactionAmount = rs.getDouble(3);
				String transactionLabel = rs.getString(4);
				
				Category category = new Category(rs.getString(8));
				category.setId(rs.getInt(7));
				
				SubCategory subCategory = new SubCategory(rs.getString(6));
				subCategory.setId(rs.getInt(5));
				subCategory.setCategory(category);
				
				
				Transaction transaction = new Transaction();
				transaction.setId(transactionId);
				transaction.setDate(transactionDate);
				transaction.setLabel(transactionLabel);
				transaction.setAmount(transactionAmount);
				
				transaction.setSubCategory(subCategory);
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return transactionList;
	}

	public static List<Transaction> findAllSpendingsClassifiedByAccountForPeriodGroupByPeriod(Account account, Period period, Date startDate, Date endDate){
		
		return findAllSpendingsClassifiedByAccountAndCategoryForPeriodGroupByPeriod(account, period, startDate, endDate, null);
	}
	
	public static List<Transaction> findAllSpendingsClassifiedByAccountAndCategoryForPeriodGroupByPeriod(Account account, Period period, Date startDate, Date endDate, Category categoryToMatch){
		
		List<Transaction> transactionList = new LinkedList<>();
		
		if(null == account || null == period){
			return transactionList;
		}
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String dateRestriction = (null != startDate && null != endDate)? " AND (tr." + COLUMN_DATE + " between ? and ? ) "  : "";
			String categoryRestriction = (null != categoryToMatch)? " AND (cat." + CategoryService.COLUMN_ID + " = ? ) "  : "";
			
			String categoryOrSubCategory = (null != categoryToMatch)? ("subCat." + SubCategoryService.COLUMN_ID) : ("cat." + CategoryService.COLUMN_ID);
			
			String selectQuery = "SELECT "
					+ "date(tr." + COLUMN_DATE + ",'start of " + period.name() + "')"
					+ ",SUM(tr." + COLUMN_AMOUNT + ")" 
					+ ",cat." + CategoryService.COLUMN_ID
					+ ",cat." + CategoryService.COLUMN_LABEL
					+ ",subCat." + SubCategoryService.COLUMN_ID
					+ ",subCat." + SubCategoryService.COLUMN_LABEL
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " INNER JOIN " + SubCategoryService.TABLE + " subCat ON subCat." + SubCategoryService.COLUMN_ID + "=tr." + COLUMN_SUB_CATEGORY_ID
					+ " INNER JOIN " + CategoryService.TABLE + " cat ON cat." + CategoryService.COLUMN_ID + "=subCat." + SubCategoryService.COLUMN_CATEGORY_ID
					+ " WHERE tr." + COLUMN_SUB_CATEGORY_ID + " is not null "
					+ " AND tr." + COLUMN_AMOUNT + " < 0 "
					+ " AND acc." + AccountService.COLUMN_ID + "=?"
					+ dateRestriction
					+ categoryRestriction
					+ " GROUP BY " + categoryOrSubCategory + ", date(tr." + COLUMN_DATE + ",'start of " + period.name() + "')"
					+ " ORDER BY " + categoryOrSubCategory + "ASC"
					+ ",tr." + COLUMN_DATE + " ASC";
			
			statement = connection.prepareStatement(selectQuery);
			
			int index = 1;
			statement.setInt(index++, account.getId());
			if(!dateRestriction.isEmpty()){
				statement.setString(index++, StringUtil.getFormattedDate(startDate, StringUtil.DB_DATE_PATTERN));
				statement.setString(index++, StringUtil.getFormattedDate(endDate, StringUtil.DB_DATE_PATTERN));
			}
			
			if(!categoryRestriction.isEmpty()){
				statement.setInt(index++, categoryToMatch.getId());
			}

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				Date transactionDate = StringUtil.toDate(rs.getString(1));
				Double transactionAmount = rs.getDouble(2);
				
				Category category = new Category(rs.getString(4));
				category.setId(rs.getInt(3));
				
				SubCategory subCategory = new SubCategory(rs.getString(6));
				subCategory.setId(rs.getInt(5));
				subCategory.setCategory(category);
				
				
				Transaction transaction = new Transaction();
				transaction.setDate(transactionDate);
				transaction.setAmount(transactionAmount);
				
				transaction.setSubCategory(subCategory);
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return transactionList;
	}

	public static List<Transaction> findAllByAccount(Account account, Date startDate, Date endDate){
	
		List<Transaction> transactionList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 
					+ ",tr." + COLUMN_DATE 
					+ ",tr." + COLUMN_AMOUNT 
					+ ",tr." + COLUMN_LABEL 
					+ " FROM " + TABLE + " tr "
					+ " WHERE tr." + COLUMN_ACCOUNT_ID + "=?"
					// les bornes sont incluses
					+ " AND tr." + COLUMN_DATE + " between ? and ?" 
					+ " ORDER BY tr." + COLUMN_DATE + "DESC"
							+ ",tr." + COLUMN_LABEL + " ASC"
							+ ",tr." + COLUMN_AMOUNT + " DESC";
			
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, account.getId());
			statement.setString(2, StringUtil.getFormattedDate(startDate, StringUtil.DB_DATE_PATTERN));
			statement.setString(3, StringUtil.getFormattedDate(endDate, StringUtil.DB_DATE_PATTERN));
	
			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int transactionId = rs.getInt(1);
				Date transactionDate = StringUtil.toDate(rs.getString(2));
				Double transactionAmount = rs.getDouble(3);
				String transactionLabel = rs.getString(4);
				
				Transaction transaction = new Transaction();
				transaction.setId(transactionId);
				transaction.setDate(transactionDate);
				transaction.setLabel(transactionLabel);
				transaction.setAmount(transactionAmount);
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}
	
		return transactionList;
	}
	
	public static List<Transaction> findAllToClassify(){
		
		List<Transaction> transactionList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 
					+ ",tr." + COLUMN_DATE 
					+ ",tr." + COLUMN_AMOUNT 
					+ ",tr." + COLUMN_LABEL 
					+ ",acc." + AccountService.COLUMN_LABEL
					+ ",acc." + AccountService.COLUMN_CURRENCY
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " WHERE tr." + COLUMN_SUB_CATEGORY_ID + " is null "
					+ " ORDER BY acc." + AccountService.COLUMN_LABEL + "ASC "
							+ ",tr." + COLUMN_DATE + "DESC"
							+ ",tr." + COLUMN_LABEL + " ASC"
							+ ",tr." + COLUMN_AMOUNT + " DESC";
			
			statement = connection.prepareStatement(selectQuery);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int transactionId = rs.getInt(1);
				Date transactionDate = StringUtil.toDate(rs.getString(2));
				Double transactionAmount = rs.getDouble(3);
				String transactionLabel = rs.getString(4);
				
				String accountLabel = rs.getString(5);
				String accountCurrency = rs.getString(6);
				
				Transaction transaction = new Transaction();
				transaction.setId(transactionId);
				transaction.setDate(transactionDate);
				transaction.setLabel(transactionLabel);
				transaction.setAmount(transactionAmount);
				
				transaction.setAccount(new Account(accountLabel, accountCurrency));
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return transactionList;
	}
	
public static List<Transaction> findAllBySubCategory(int subCategoryId){
		
		List<Transaction> transactionList = new LinkedList<>();
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			
			String selectQuery = "SELECT "
					+ "tr." + COLUMN_ID 
					+ ",tr." + COLUMN_DATE 
					+ ",tr." + COLUMN_AMOUNT 
					+ ",tr." + COLUMN_LABEL 
					+ ",acc." + AccountService.COLUMN_LABEL
					+ " FROM " + TABLE + " tr "
					+ " INNER JOIN " + AccountService.TABLE + " acc ON acc." + AccountService.COLUMN_ID + "=tr." + COLUMN_ACCOUNT_ID
					+ " WHERE tr." + COLUMN_SUB_CATEGORY_ID + " = ? "
					+ " ORDER BY acc." + AccountService.COLUMN_LABEL + "ASC "
							+ ",tr." + COLUMN_DATE + "DESC"
							+ ",tr." + COLUMN_LABEL + " ASC"
							+ ",tr." + COLUMN_AMOUNT + " DESC";
			
			statement = connection.prepareStatement(selectQuery);
			
			statement.setInt(1, subCategoryId);

			rs = statement.executeQuery();
			
			while(rs.next()){
				
				int transactionId = rs.getInt(1);
				Date transactionDate = StringUtil.toDate(rs.getString(2));
				Double transactionAmount = rs.getDouble(3);
				String transactionLabel = rs.getString(4);
				
				String accountLabel = rs.getString(5);
				
				Transaction transaction = new Transaction();
				transaction.setId(transactionId);
				transaction.setDate(transactionDate);
				transaction.setLabel(transactionLabel);
				transaction.setAmount(transactionAmount);
				
				transaction.setAccount(new Account(accountLabel));
				
				transactionList.add(transaction);
			}
		} 
		catch(Exception e){
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(statement);
		}

		return transactionList;
	}
	
	/**
	 * Supprime la {@link Transaction}
	 * @param transaction
	 */
	public static void delete(Transaction transaction){
		
		if(null != transaction && transaction.getId() != 0){
			
			Connection connection = SqLiteConnection.getInstance().getConnection();
			PreparedStatement statement = null;
			
			try{
			
				String deleteQuery = "DELETE FROM " + TABLE + " WHERE " + COLUMN_ID + "=?";
				statement = connection.prepareStatement(deleteQuery);
				statement.setInt(1, transaction.getId());
				
				statement.execute();
				
				if(transaction.getSubCategory() == null){
					Session.getInstance().decrementTransactionsToClassify();
				}
				
			}catch(Exception e){
				System.err.println(e.getMessage());
			} finally {
				DbUtil.closeStatement(statement);
			}
		}
	}
	
	/**
	 * Sauvegarde la {@link Transaction}
	 * @param transaction
	 */
	public static void save(Transaction transaction){
		
		if(null != transaction){
			
			if(transaction.getId() == 0){
				insert(transaction);
			} else {
				update(transaction);
			}
		}
	}
	
	private static void insert(Transaction transaction){
	
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String insertQuery = "INSERT INTO " + TABLE + " (" + 
															COLUMN_DATE + "," + 
															COLUMN_AMOUNT +"," + 
															COLUMN_LABEL + ", " +
															COLUMN_IDENTIFIER + ", " +
															COLUMN_ACCOUNT_ID + ") " + 
								"SELECT ?,?,?,?,? " +
								" WHERE NOT EXISTS (" +
													" SELECT 0 FROM " + TABLE + 
													" WHERE " + COLUMN_DATE + " = ? " + 
													" AND " + COLUMN_AMOUNT + " = ? " + 
													" AND " + COLUMN_LABEL + " = ?" +
													" AND " + COLUMN_IDENTIFIER + " = ?" +
													" AND " + COLUMN_ACCOUNT_ID + " = ?" +
													");";
			statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			
			int index=1;
			statement.setString(index++, StringUtil.getFormattedDate(transaction.getDate(), StringUtil.DB_DATE_PATTERN));
			statement.setString(index++, String.valueOf(transaction.getAmount()));
			statement.setString(index++, transaction.getLabel());
			statement.setString(index++, transaction.getIdentifier());
			statement.setInt(index++, transaction.getAccount().getId());
			statement.setString(index++, StringUtil.getFormattedDate(transaction.getDate(), StringUtil.DB_DATE_PATTERN));
			statement.setString(index++, String.valueOf(transaction.getAmount()));
			statement.setString(index++, transaction.getLabel());
			statement.setString(index++, transaction.getIdentifier());
			statement.setInt(index++, transaction.getAccount().getId());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	int transactionId = generatedKeys.getInt(1);
	            	transaction.setId(transactionId);
	            	if(transactionId > 0){
	            		Session.getInstance().incrementTransactionsToClassify();
	            	}
	            }
	            else {
	                throw new SQLException("Creating transaction failed, no ID obtained.");
	            }
	        }
		} 
		catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			DbUtil.closeStatement(statement);
		}
	}
	
	private static void update(Transaction transaction){
		
		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + 
											COLUMN_DATE + "=?, " + 
											COLUMN_AMOUNT + "=?, " + 
											COLUMN_LABEL + "=?, " +
											COLUMN_ACCOUNT_ID + "=? " + 
								" WHERE " + COLUMN_ID + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setString(index++, StringUtil.getFormattedDate(transaction.getDate(), StringUtil.DB_DATE_PATTERN));
			statement.setString(index++, String.valueOf(transaction.getAmount()));
			statement.setString(index++, transaction.getLabel());
			statement.setInt(index++, transaction.getAccount().getId());
			statement.setInt(index++, transaction.getId());
			
			statement.executeUpdate();
		} 
		catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			DbUtil.closeStatement(statement);
		}
	}

	public static void classify(int transactionId, int subCategoryId) {

		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + 
											COLUMN_SUB_CATEGORY_ID + "=? " + 
								" WHERE " + COLUMN_ID + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setInt(index++, subCategoryId);
			statement.setInt(index++, transactionId);
			
			statement.executeUpdate();
			
			if(transactionId > 0){
				Session.getInstance().decrementTransactionsToClassify();
			}
		} 
		catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			DbUtil.closeStatement(statement);
		}
		
	}
	
	public static void unclassify(int transactionId) {

		Connection connection = SqLiteConnection.getInstance().getConnection();
		PreparedStatement statement = null;
		
		try{
			
			String updateQuery = "UPDATE " + TABLE + " SET " + 
											COLUMN_SUB_CATEGORY_ID + "= null " + 
								" WHERE " + COLUMN_ID + "=?";
			statement = connection.prepareStatement(updateQuery);
			
			int index=1;
			statement.setInt(index++, transactionId);
			
			statement.executeUpdate();
			
			if(transactionId > 0){
				Session.getInstance().incrementTransactionsToClassify();
			}
		} 
		catch(Exception e){
			System.err.println(e.getMessage());
		} finally {
			DbUtil.closeStatement(statement);
		}
		
	}
	
	public static void unclassify(Transaction transaction) {
		if(null != transaction && transaction.getId() != 0 && null != transaction.getSubCategory() && transaction.getSubCategory().getId() != 0){
			unclassify(transaction.getId());
		}
	}

}
