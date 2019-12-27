package fr.coward.main.model;

import java.util.HashMap;
import java.util.Map;

public class MenuContext {
	
	private static MenuContext INSTANCE = null;
	
	private Map<String, Object> initObjectMap;
	
	private MenuContext(){
		this.initObjectMap = new HashMap<>();
	}
	
	public static synchronized MenuContext getInstance(){
		
		if(INSTANCE == null){
			INSTANCE = new MenuContext();
		}
		
		return INSTANCE;
		
	}
	
	public void addInitObj(String propertyName, Object obj){
			this.initObjectMap.put(propertyName, obj);
	}
	
	public boolean hasObjects(){
		return !initObjectMap.isEmpty();
	}

	/**
	 * Renvoie l'objet demandé
	 * @param propertyName
	 * @param eraseProperty si true, retire l'objet
	 * @return
	 */
	public Object getInitObj(String propertyName, boolean eraseProperty){
		
		Object object = this.initObjectMap.get(propertyName);
		if(eraseProperty){
			this.initObjectMap.remove(propertyName);
		}
		
		return object;
	}
	
	/**
	 * Renvoie l'objet demandé puis le retire
	 * @param propertyName
	 * @return
	 */
	public Object getInitObj(String propertyName){
		return this.getInitObj(propertyName, true);
	}

}
