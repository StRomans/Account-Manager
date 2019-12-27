package fr.coward.main.ui.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskProcessor extends Service<String> {
	
	

	@Override
	protected Task<String> createTask() {
		
		return new Task<String>() {
			
            @Override protected String call() throws Exception {
            	            	
                return null;
            }
        };
	}

}
