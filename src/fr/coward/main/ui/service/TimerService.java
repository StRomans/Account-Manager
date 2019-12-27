package fr.coward.main.ui.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TimerService extends Service<String> {
	
	private long duration;
	
	public TimerService(long duration){
		this.duration = duration;
	}
	
    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override protected String call() throws Exception {
            	
            	Thread.sleep(duration);
            	
                return null;
            }
        };
    }
}
