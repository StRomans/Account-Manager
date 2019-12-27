package fr.coward.main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.coward.main.model.rule.RuleCriteria;
import fr.coward.main.model.rule.RuleOperator;
import fr.coward.main.model.utils.Pair;

public class Rule{

	private int id;
	private int priority;
	private List<Pair<RuleCriteria, String>> criteriaList;
	
	// mandatory only if criteria.size > 1
	private RuleOperator intraOperator;
	
	private SubCategory subCategory;
	
	public Rule() {
		this.id = 0;
		this.criteriaList = new ArrayList<Pair<RuleCriteria,String>>();
		this.subCategory = null;
	}

	public Rule(SubCategory expectedResult, int priority) throws Exception{
		this();
		if(expectedResult.getId() == 0){
			throw new Exception("Invalid subCategory !");
		}
		
		this.priority = priority;
		this.subCategory = expectedResult;
	}
	
	public String serializeValidators(){
		
		String validators = "";
		
		for(Pair<RuleCriteria, String> pair : criteriaList){
			validators += "[" + pair.getL().name() + ":" + pair.getR() + "]";
		}
		
		return validators;
	}
	
	public void deserializeValidators(String validators){
		
		Pattern pattern = Pattern.compile("\\[([^\\[\\]]+):([^\\[\\]]+)\\]+");
		
		Matcher matcher = pattern.matcher(validators);
		
		while(matcher.find()){
			String criteriaName = matcher.group(1);
			String value = matcher.group(2);
			
			this.getCriteriaList().add(new Pair<RuleCriteria, String>(RuleCriteria.valueOf(criteriaName), value));
		}
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
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the criteriaList
	 */
	public List<Pair<RuleCriteria, String>> getCriteriaList() {
		return criteriaList;
	}

	/**
	 * @param criteriaList the criteriaList to set
	 */
	public void setCriteriaList(List<Pair<RuleCriteria, String>> criteriaList) {
		this.criteriaList = criteriaList;
	}

	/**
	 * @return the intraOperator
	 */
	public RuleOperator getIntraOperator() {
		return intraOperator;
	}

	/**
	 * @param intraOperator the intraOperator to set
	 */
	public void setIntraOperator(RuleOperator intraOperator) {
		this.intraOperator = intraOperator;
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
		
		str += "Priorité " + getPriority();
		
		
		String criteria = "\n\tSI ";
		boolean isFirst = true;
		for(Pair<RuleCriteria, String> pair : this.getCriteriaList()){
			if(!isFirst){
				criteria += "\n\t" + this.getIntraOperator().toString() + " ";
			}
			criteria += pair.toString(); 
			isFirst = false;
		}
		
		str += criteria;
		str += "\n\tALORS la transaction est classée dans --> " + this.getSubCategory().toString();
		
		return str;
	}
}
