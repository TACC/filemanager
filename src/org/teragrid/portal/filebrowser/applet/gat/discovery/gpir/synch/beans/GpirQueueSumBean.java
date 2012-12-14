package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.synch.beans;

public class GpirQueueSumBean {
    private int total;
    private int running;
    private int waiting;
    private int held;
    private int stopped;
    private int migrating;
    
    private String queueName;
    
    public GpirQueueSumBean() {
        
    }

    /**
     * @return the held
     */
    public int getHeld() {
        return held;
    }


    /**
     * @param held the held to set
     */
    public void setHeld(int held) {
        this.held = held;
    }


    /**
     * @return the migrating
     */
    public int getMigrating() {
        return migrating;
    }


    /**
     * @param migrating the migrating to set
     */
    public void setMigrating(int migrating) {
        this.migrating = migrating;
    }


    /**
     * @return the queueName
     */
    public String getQueueName() {
        return queueName;
    }


    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }


    /**
     * @return the running
     */
    public int getRunning() {
        return running;
    }


    /**
     * @param running the running to set
     */
    public void setRunning(int running) {
        this.running = running;
    }


    /**
     * @return the stopped
     */
    public int getStopped() {
        return stopped;
    }


    /**
     * @param stopped the stopped to set
     */
    public void setStopped(int stopped) {
        this.stopped = stopped;
    }


    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }


    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }


    /**
     * @return the waiting
     */
    public int getWaiting() {
        return waiting;
    }


    /**
     * @param waiting the waiting to set
     */
    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }
    
    
    
}
