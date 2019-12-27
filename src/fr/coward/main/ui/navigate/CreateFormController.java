/**
 * 
 */
package fr.coward.main.ui.navigate;

import java.net.URL;
import java.util.ResourceBundle;

import fr.coward.main.ui.service.TimerService;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * @author Coward
 *
 */
public abstract class CreateFormController implements Initializable, ICreateForm {
	
	@FXML
	protected Label errorLabel;
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	public void initialize(URL location, ResourceBundle resources) {
		
		initComponents(true);
	}
	

	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.ICreateForm#initComponents(boolean)
	 */
	public abstract void initComponents(boolean isFirstInit);
	
	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.ICreateForm#validate()
	 */
	public abstract void validate() throws Exception;
	
	/**
	 * Effectue l'insertion/update des données
	 * @throws Exception
	 */
	public abstract void doSave() throws Exception;
	
	/* (non-Javadoc)
	 * @see fr.coward.main.ui.navigate.ICreateForm#save()
	 */
	@Override
	public boolean save() {
		
		try {
			
			validate();
			
			doSave();
			
			return true;
			
		} catch (Exception e) {
			setMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Affiche un message d'avertissement
	 * @param errorMsg
	 */
	protected void setMessage(String errorMsg){
		
		if(null != errorLabel){
			errorLabel.setText(errorMsg);
			
			TimerService timer = new TimerService(5000);
	        timer.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	            @Override
	            public void handle(WorkerStateEvent event) {
	            	eraseErrorMsg();
	            }
	        });
	         
	        timer.start();
		}
	}
	
	
	/**
	 * Efface le message d'avertissement
	 */
	protected void eraseErrorMsg(){
		setMessage("");
	}
	
	/**
	 * Demande le focus sur le composant
	 * @param component le composant
	 */
	public void focus(Node component){
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	if(null != component){
		    		component.requestFocus();
		    	}
		    }
		});
	}

}
