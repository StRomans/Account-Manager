package fr.coward.main.model.DTO;

import java.util.Date;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import fr.coward.main.model.Account;
import fr.coward.main.model.Category;
import fr.coward.main.model.SubCategory;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.TransactionService;

public class TransactionViewDTO {
	
	private IntegerProperty id;
	private StringProperty label;
	private ObjectProperty<Date> date;
	private DoubleProperty amount;
	private StringProperty identifier;
	
	private ObjectProperty<SubCategory> subCategory;

	private ObjectProperty<Category> category;

	private ObjectProperty<Account> account;

	public TransactionViewDTO() {
		this.id = new SimpleIntegerProperty(0);
		this.label = new SimpleStringProperty();
		this.date = new SimpleObjectProperty<>();
		this.amount = new SimpleDoubleProperty();
		this.identifier = new SimpleStringProperty();
		
		this.subCategory = new SimpleObjectProperty<>();

		this.category = new SimpleObjectProperty<>();
		
		this.account = new SimpleObjectProperty<>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id.get();
	}
	
	public IntegerProperty getIdProperty() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id.set(id);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label.get();
	}
	
	public StringProperty getLabelProperty() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label.set(label);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date.get();
	}
	
	public ObjectProperty<Date> getDateProperty() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date.set(date);
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount.get();
	}
	
	public DoubleProperty getAmountProperty() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount.set(amount);
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier.get();
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier.set(identifier);
	}
	
	/**
	 * @param subCategory the subCategory to set
	 */
	public void setSubCategory(SubCategory subCategory) {
		this.subCategory.set(subCategory);
	}
	
	/**
	 * @param subCategory the subCategory to set
	 */
	public ObjectProperty<SubCategory> getSubCategoryProperty() {
		return this.subCategory;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category.set(category);
	}
	
	/**
	 * @param category the category to get
	 */
	public ObjectProperty<Category> getCategoryProperty() {
		return this.category;
	}
	
	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account.set(account);
	}
	
	/**
	 * @param account the account to get
	 */
	public ObjectProperty<Account> getAccountProperty() {
		return this.account;
	}

	/**
	 * @return the accountId
	 */
	public int getAccountId() {
		return this.account.get().getId();
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return this.account.get().getCurrency();
	}
	
	public StringProperty getCurrencyProperty() {
		return new SimpleStringProperty(getCurrency());
	}

	public void delete(){
		Transaction transactionToDelete = new Transaction();
		transactionToDelete.setId(getId());
		TransactionService.delete(transactionToDelete);
	}
	
	public void unclassify(){
		TransactionService.unclassify(toTransaction());
		this.setSubCategory(null);
		this.setCategory(null);
	}
	
	public void classify(SubCategory subCategory){
		this.setSubCategory(subCategory);
		this.setCategory(subCategory.getCategory());
		TransactionService.classify(this.getId(), subCategory.getId());
	}
	
	public Transaction toTransaction(){
		
		Transaction transaction = new Transaction();
		
		transaction.setId(this.getId());
		transaction.setAmount(getAmount());
		transaction.setDate(getDate());
		transaction.setLabel(getLabel());
		transaction.setSubCategory(this.subCategory.get());
		
		transaction.setAccount(this.account.get());
		
		return transaction;
	}
}
