package fr.coward.main.model;


public class Settings {
	
	private String code;
	private String value;

	public Settings(String code){
		this(code, null);
	}
	
	public Settings(String code, String value){
		this.setCode(code);
		this.setValue(value);
	}
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
