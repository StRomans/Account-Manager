package fr.coward.main.model.utils;

public class StopWatch {

    /* Private Instance Variables */
    /** Stores the start time when an object of the StopWatch class is initialized. */
    private long startTime;
    private String label;

    /**
     * Custom constructor which initializes the {@link #startTime} parameter.
     */
    public StopWatch() {
        startTime = System.currentTimeMillis();
        this.label = "";
    }
    
    public StopWatch(String label) {
    	this();
    	this.label = label;
    }

    /**
     * Gets the elapsed time (in seconds) since the time the object of StopWatch was initialized.
     * 
     * @return Elapsed time in seconds.
     */
    public double getElapsedTime() {
        long endTime = System.currentTimeMillis();
        return (double) (endTime - startTime) / (1000);
    }
    
    public void printElapsedTime(){
    	System.out.println(this.label + " : " + getElapsedTime() + " seconds");
    }
}