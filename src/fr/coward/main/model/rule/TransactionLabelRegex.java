package fr.coward.main.model.rule;

import java.util.regex.Pattern;

import fr.coward.main.model.Transaction;

public class TransactionLabelRegex extends AbstractRuleValidator<Transaction> {
	
	
	public TransactionLabelRegex(){
		super();
	}

	public TransactionLabelRegex(String userValue) throws Exception {
		super(userValue);
		
		this.setUserValue(userValue);
	}
	
	@Override
	public void setUserValue(String userValue) throws Exception {
		super.setUserValue(userValue);
		
		// Teste la validité de la regex
		Pattern.compile(userValue);
	}

	@Override
	public boolean isValid(Transaction transaction) {
		
		String regex = super.getUserValue();
		
		return Pattern.matches(regex, transaction.getLabel());
	}

	
	@Override
	public String toString() {
		return RuleCriteria.valueOf(this.getClass().getSimpleName()).getIhmLabel() + " " + super.getUserValue();
	}
}
