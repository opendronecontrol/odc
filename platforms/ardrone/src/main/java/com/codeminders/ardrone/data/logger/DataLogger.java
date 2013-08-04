package com.codeminders.ardrone.data.logger;

public interface DataLogger {
    
    void log(ChannelDataChunk data);
    
    void logStreamContent(int data);
    
    public void finish();
}
