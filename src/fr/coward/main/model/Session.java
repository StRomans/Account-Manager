package fr.coward.main.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import fr.coward.main.model.services.SettingsService;
import fr.coward.main.model.services.TransactionService;
import fr.coward.main.model.services.SettingsService.Option;

public class Session {
	
	private static Session INSTANCE = null;
	
	private SimpleIntegerProperty unclassifiedTransactionCount;
	
	private SimpleStringProperty theme;
	
	private Session(){
		
		unclassifiedTransactionCount = new SimpleIntegerProperty(TransactionService.findAllToClassify().size());
		theme = new SimpleStringProperty(SettingsService.findByCode(Option.THEME).getValue());
	}
	
	public static synchronized Session getInstance(){
		
		if(INSTANCE == null){
			INSTANCE = new Session();
		}
		
		return INSTANCE;
		
	}
	
	public SimpleIntegerProperty getUnclassifiedTransactionCountProperty(){
		return this.unclassifiedTransactionCount;
	}
	
	public void incrementTransactionsToClassify(){
		unclassifiedTransactionCount.set(unclassifiedTransactionCount.get()+1);
	}
	
	public void decrementTransactionsToClassify(){
		unclassifiedTransactionCount.set(unclassifiedTransactionCount.get()-1);
	}
	
	public void addTransactionPropertyChangeListener(ChangeListener<? super Number> listener){
		unclassifiedTransactionCount.addListener(listener);
	}
	
	public void addThemePropertyChangeListener(ChangeListener<? super String> listener){
		theme.addListener(listener);
	}
	
	public SimpleStringProperty getThemeProperty(){
		return this.theme;
	}

}
