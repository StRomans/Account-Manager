package fr.coward.main.model.rule;

import java.util.GregorianCalendar;

import fr.coward.main.model.Transaction;

public class TransactionDayOfMonthEquals extends AbstractRuleValidator<Transaction> {
	
	public TransactionDayOfMonthEquals(){
		super();
	}

	public TransactionDayOfMonthEquals(String userValue) throws Exception {
		super(userValue);
		
		this.setUserValue(userValue);
	}

	
	@Override
	public void setUserValue(String userValue) throws Exception {
		
		int intUserValue = Integer.parseInt(userValue);
		
		if(intUserValue <= 0 || intUserValue > 31){
			throw new Exception("Jour du mois invalide");
		}
		
		super.setUserValue(userValue);
	}
	
	@Override
	public boolean isValid(Transaction transaction) {

		int dayOfMonthToEqualize = Integer.parseInt(super.getUserValue());
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(transaction.getDate());
		
		return calendar.get(GregorianCalendar.DAY_OF_MONTH) == dayOfMonthToEqualize;
	}

	
	@Override
	public String toString() {
		return RuleCriteria.valueOf(this.getClass().getSimpleName()).getIhmLabel() + " " + super.getUserValue();
	}
}
