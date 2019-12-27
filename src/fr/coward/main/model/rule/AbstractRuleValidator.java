package fr.coward.main.model.rule;

public abstract class AbstractRuleValidator<Transaction> {
	
	private String userValue;
	
	public AbstractRuleValidator(){
	};
	
	public AbstractRuleValidator(String userValue) throws Exception{
		this();
		this.userValue = userValue;
	}
	
	public abstract boolean isValid(Transaction criteriaValue);

	/**
	 * @return the userValue
	 */
	public String getUserValue() {
		return userValue;
	}

	/**
	 * @param string the userValue to set
	 * @throws Exception 
	 */
	public void setUserValue(String string) throws Exception {
		this.userValue = string;
	}
}
