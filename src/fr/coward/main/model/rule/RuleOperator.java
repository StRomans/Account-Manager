package fr.coward.main.model.rule;

public enum RuleOperator{
	
	AND("ET", true), OR("OU", false);
	
	private String ihmLabel;
	private boolean initBoolValue;
	
	private RuleOperator(String ihmLabel, boolean initBoolValue) {
		this.ihmLabel = ihmLabel;
		this.initBoolValue = initBoolValue;
	}

	public String getIhmLabel(){
		return this.ihmLabel;
	}
	
	public boolean getInitValue(){
		return this.initBoolValue;
	}
	
	@Override
	public String toString() {
		return this.getIhmLabel();
	}
}
