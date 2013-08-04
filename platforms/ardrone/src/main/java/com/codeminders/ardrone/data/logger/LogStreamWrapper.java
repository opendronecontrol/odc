package com.codeminders.ardrone.data.logger;

import java.io.IOException;
import java.io.InputStream;

public class LogStreamWrapper extends InputStream {
    
    InputStream     dataStream;
    DataLogger      logger;

    public LogStreamWrapper(InputStream dataStream, DataLogger logger) {
       this.dataStream = dataStream;
       this.logger = logger;
    }

    @Override
    public int read() throws IOException {
        int data = dataStream.read();
        logger.logStreamContent(data);
        return data;
    }

}
