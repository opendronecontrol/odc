package com.codeminders.ardrone.data;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class DataDecoder extends Thread {
    
    private Logger             log = Logger.getLogger(this.getClass().getName());
    
    private ARDroneDataReader  datareader;
    private boolean            pauseFlag;
    

    @Override
    public synchronized void start() {
        super.start();
        if (null == datareader) {
            throw new RuntimeException("No reading thread is arrached"); 
        }
    }

    protected void pauseCheck(){
        if (pauseFlag) {
            synchronized(this) {
                if (pauseFlag) {
                   try {
                       wait();
                    } catch (InterruptedException e) {
                        log.log(Level.SEVERE, "Pause is interrupted", e);
                    }
                }
            }
        }
    }
    
    protected void setDataReader(ARDroneDataReader datareader) {
        if (this.isAlive()) {
            throw new RuntimeException("Rading Thrad already started. You can't change stream on fly"); 
        }
        this.datareader = datareader;
    }

    public ARDroneDataReader getDataReader() {
        return datareader;
    }    
    
    public synchronized void pauseDecoding() {
        pauseFlag = true;
    }
    
    public synchronized void resumeDecoding() {
        pauseFlag = false;
        notify();
    }
    
    public abstract void finish();    

}
