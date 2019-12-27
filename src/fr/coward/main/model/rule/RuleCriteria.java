package fr.coward.main.model.rule;

import javafx.scene.control.Control;

import com.jfoenix.controls.JFXTextField;

import fr.coward.main.model.Transaction;
import fr.coward.main.ui.components.NumericField;


public enum RuleCriteria {
	
	TransactionDayOfMonthEquals("Le numéro du jour dans le mois est égal à", fr.coward.main.model.rule.TransactionDayOfMonthEquals.class,NumericField.class),
	AccountLabelRegex("Le libellé du compte match la regex", fr.coward.main.model.rule.AccountLabelRegex.class, JFXTextField.class),
	TransactionLabelRegex("Le libellé de la transaction match la regex", fr.coward.main.model.rule.TransactionLabelRegex.class, JFXTextField.class),
	TransactionAmountLowerThan("Le montant (en valeur absolue) de la transaction est inférieur à", fr.coward.main.model.rule.TransactionAmountLowerThan.class, NumericField.class);
	
	private String ihmLabel;
	private Class<? extends AbstractRuleValidator<Transaction>> validatorClass;
	private Class<? extends Control> displayedControl;
	
	private RuleCriteria(String ihmLabel, Class<? extends AbstractRuleValidator<Transaction>> validatorClass, Class<? extends Control> displayedControl) {
		this.ihmLabel = ihmLabel;
		this.validatorClass = validatorClass;
		this.displayedControl = displayedControl;
	}

	public String getIhmLabel(){
		return this.ihmLabel;
	}
	
	public Class<? extends AbstractRuleValidator<Transaction>> getValidationClass(){
		return this.validatorClass;
	}
	
	public  Class<? extends Control> getControlClass(){
		return this.displayedControl;
	}
	
	@Override
	public String toString() {
		return this.getIhmLabel();
	}
	
	public static RuleCriteria valueOf(Class<?> validatorClass){
		
		RuleCriteria criteriaFound = null;
		
		for(RuleCriteria criteria : values()){
			if(criteria.getValidationClass().equals(validatorClass)){
				return criteria;
			}
		}
			
		return criteriaFound;
	}
}
