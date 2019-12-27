package fr.coward.main.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import fr.coward.main.ui.utils.StringUtil;

public class Transaction {
	
	private int id;
	private Date date;
	private double amount;
	private String label;
	private String identifier;
	
	private SubCategory subCategory;
	
	private Account account;
	
	
	public Transaction(){
		this.id = 0;
	}
	
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}



	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}



	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	
	/**
	 * @return the subCategory
	 */
	public SubCategory getSubCategory() {
		return subCategory;
	}



	/**
	 * @param subCategory the subCategory to set
	 */
	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}



	@Override
	public String toString() {
		
		String str = "";
		String currency = (null != this.account.getCurrency()) ? this.account.getCurrency() : "?";
		
		if(null != this.getAccount() && StringUtil.isNotNullNotEmpty(this.getAccount().getLabel())){
			str += this.getAccount().getLabel() + " - ";
		}
		
		str += StringUtil.getFormattedDate(this.getDate(), "dd/MM/yyyy") + " ";
		str += this.getAmount() + " " + currency + " - ";
		str += this.getLabel();
		
		return str;
	}
	
	public static BigDecimal sum(List<Transaction> transactions){
		return sum(transactions, null);
	}
	
	public static BigDecimal sumSpendings(List<Transaction> transactions){
		return sum(transactions, false);
	}
	
	public static BigDecimal sumIncomes(List<Transaction> transactions){
		return sum(transactions, true);
	}
	
	private static BigDecimal sum(List<Transaction> transactions, Boolean income){
		
		BigDecimal total = BigDecimal.ZERO;
		for(Transaction transaction : transactions){
			
			if((null == income || !income) && Double.compare(transaction.getAmount(), 0) < 0){
				total = total.add(BigDecimal.valueOf(transaction.getAmount()).abs());
			}
			
			else if((null == income || income) && Double.compare(transaction.getAmount(), 0) > 0){
				total = total.add(BigDecimal.valueOf(transaction.getAmount()).abs());
			}
		}
		
		return total;
	}

}
