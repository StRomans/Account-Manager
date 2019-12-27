package fr.coward.main.model;

import fr.coward.main.model.services.TransactionService;

public class SubCategory implements Comparable<SubCategory> {
	
	private int id;
	private String label;
	private Category category;
	
	private int transientTransactionNumber;
	
	public SubCategory(){
		this.id = 0;
	}
	
	public SubCategory(String label) {
		this();
		this.label = label;
		this.transientTransactionNumber = 0;
	}
	
	public SubCategory(int id, String label, Category category) {
		this(label);
		this.setId(id);
		this.setCategory(category);
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
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		
		String str = "";
		
		if(null != this.getCategory()){
			str += this.getCategory() + " - ";
		}
		
		str += this.getLabel();
		
		return str;
	}
	
	public String toStringForClassificationScreen(){
		
		String str = this.toString();
		
		if(0 > this.transientTransactionNumber){
			str += " (" + this.transientTransactionNumber + ")";
		}
		
		return str;
	}
	
	public void classify(String transactionId){
		
		this.transientTransactionNumber++;
		
		TransactionService.classify(Integer.valueOf(transactionId), this.getId());
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
		SubCategory other = (SubCategory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(SubCategory o) {
		return this.toString().compareTo(o.toString());
	}
}
