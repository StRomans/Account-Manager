package fr.coward.main.model.rule;

import fr.coward.main.model.Transaction;


public class TransactionAmountLowerThan extends AbstractRuleValidator<Transaction> {
	
	public TransactionAmountLowerThan(){
		super();
	}

	public TransactionAmountLowerThan(String userValue) throws Exception {
		super(userValue);
	}
	
	@Override
	public void setUserValue(String userValue) throws Exception {
		
		if(Double.parseDouble(userValue) <= 0){
			throw new Exception("Montant invalide");
		}
		
		super.setUserValue(userValue);
	}

	@Override
	public boolean isValid(Transaction transaction) {
		
		double valueToLower = Double.parseDouble(super.getUserValue());
		
		return Math.abs(transaction.getAmount()) <= valueToLower;
	}

	@Override
	public String toString() {
		return RuleCriteria.valueOf(this.getClass().getSimpleName()).getIhmLabel() + " " + super.getUserValue();
	}

}
