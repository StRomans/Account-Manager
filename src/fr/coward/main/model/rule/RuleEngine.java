package fr.coward.main.model.rule;

import java.util.List;

import fr.coward.main.model.Rule;
import fr.coward.main.model.Transaction;
import fr.coward.main.model.services.RuleService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.utils.Pair;

public class RuleEngine {

	public static void classify(List<Transaction> transactionList){
		
		List<Rule> rulesList = RuleService.findAll();
		for(Rule rule : rulesList){
			
			for(Transaction transactionToClassify : transactionList){
				
				boolean isValidRule = rule.getIntraOperator().getInitValue();
				
				for(Pair<RuleCriteria, String> pair : rule.getCriteriaList()){
					
					try {
						AbstractRuleValidator<Transaction> validator = pair.getL().getValidationClass().newInstance();
						validator.setUserValue(pair.getR());
						boolean islocalEvaluationValid = validator.isValid(transactionToClassify);
						
						if(rule.getIntraOperator().equals(RuleOperator.AND)){
							isValidRule &= islocalEvaluationValid;
						} else if(rule.getIntraOperator().equals(RuleOperator.OR)){
							isValidRule |= islocalEvaluationValid;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(isValidRule){
					TransactionService.classify(transactionToClassify.getId(), rule.getSubCategory().getId());
				}
			}
			
		}
	}
}
