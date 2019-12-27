package fr.coward.main.model;

import java.util.LinkedList;

public class Account {

	private int id;
	private String label;
	private String currency;
	private LinkedList<Transaction> transactions;
	
	public Account(){
		
		this.id = 0;
	}
	
	public Account(String accountLabel) {
		this();
		this.label = accountLabel;
	}
	
	public Account(String accountLabel, String accountCurrency) {
		this(accountLabel);
		this.currency = accountCurrency;
	}
	
	public Account(int id, String accountLabel, String accountCurrency) {
		this(accountLabel, accountCurrency);
		this.setId(id);
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
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public void addTransaction(Transaction transaction){
		
		if(null == this.transactions){
			this.transactions = new LinkedList<>();
		}
		
		if(!this.transactions.contains(transaction)){
			transaction.setAccount(this);
			this.transactions.add(transaction);
		}
	}
	
	@Override
	public String toString() {
		return getLabel() + " - " + getCurrency();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
