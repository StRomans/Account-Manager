package fr.coward.main.model.utils;

import java.util.GregorianCalendar;

public enum Period {

	MONTH("Mois", GregorianCalendar.MONTH), YEAR("Année", GregorianCalendar.YEAR), ALL("Tout l'historique", null);
	
	private String ihmLabel;
	private Integer gregorianCalendarField;
	
	Period(String ihmLabel, Integer gregorianCalendarField){
		this.ihmLabel = ihmLabel;
		this.gregorianCalendarField = gregorianCalendarField;
	}
	
	public String getIhmLabel(){
		return this.ihmLabel;
	}
	
	public Integer getGregorianCalendarField(){
		return this.gregorianCalendarField;
	}
	
	@Override
	public String toString() {	
		return this.getIhmLabel();
	}
	
	
}
