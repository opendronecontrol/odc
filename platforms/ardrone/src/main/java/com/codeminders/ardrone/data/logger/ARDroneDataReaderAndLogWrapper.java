package com.codeminders.ardrone.data.logger;

import java.io.IOException;
import java.io.InputStream;

import com.codeminders.ardrone.data.ARDroneDataReader;

public class ARDroneDataReaderAndLogWrapper implements ARDroneDataReader {
    
    ARDroneDataReader     reader;
    DataLogger            logger;

    public ARDroneDataReaderAndLogWrapper(ARDroneDataReader reader, DataLogger logger) {
        super();
        this.reader = reader;
        this.logger = logger;
    }

    @Override
    public int readDataBlock(byte[] buf) throws IOException {
        int len = reader.readDataBlock(buf);
        if (len > 0) {
            byte[] data = new byte[len];
            System.arraycopy(buf,0, data, 0, len);
            logger.log(new ChannelDataChunk(data, System.currentTimeMillis()));
        }
        return len;
    }

    @Override
    public InputStream getDataStream() {
        return new LogStreamWrapper(reader.getDataStream(), logger);
    }

    @Override
    public boolean isStreamSupported() {
        return reader.isStreamSupported();
    }

    @Override
    public void reconnect() throws IOException {
        reader.reconnect();
    }

    @Override
    public void finish() {
        reader.finish();
    }    
}
