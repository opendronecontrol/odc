package com.codeminders.ardrone.data;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ChannelProcessor {
    
    private Logger        log = Logger.getLogger(getClass().getName());
    ARDroneDataReader     reader;
    DataDecoder           decoder;
    
    private static int    STOP_TIMEOUT = 3000; // 3 sec.
    
    public ChannelProcessor(ARDroneDataReader reader, DataDecoder decoder) {
        super();
        this.reader = reader;
        this.decoder = decoder;
        
        decoder.setDataReader(reader); // decoder and reader is now linked        
        decoder.start();
    }
    
    public  void finish() {
        decoder.finish();
        try {
            decoder.join(STOP_TIMEOUT);
        } catch (InterruptedException e) {
            log.log(Level.FINEST, "Waiting till decoder is stopped is interrupted", e);
        }
        reader.finish();
    }
    
    public void pause() {
        decoder.pauseDecoding();
    }
    
    public void resume() {
        decoder.pauseDecoding();
    }
}
