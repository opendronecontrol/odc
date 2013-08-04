package com.codeminders.ardrone.data;

import java.io.IOException;
import java.io.InputStream;

public interface ARDroneDataReader {
    /**
     * @param buf input data buffer to read;
     * @return length of data that is obtained from ardrone
     * @throws IOException
     */
    public int readDataBlock(byte[] buf) throws IOException;
    
    public InputStream getDataStream();
    
    public boolean isStreamSupported();
    
    public void reconnect() throws IOException;
    
    public void finish();
}
