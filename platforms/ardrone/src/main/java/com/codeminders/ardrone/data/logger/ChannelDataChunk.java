package com.codeminders.ardrone.data.logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChannelDataChunk {
    
    byte[] data;
    long timemark;
    
    public ChannelDataChunk(byte[] data, long timemark) {
        super();
        this.data = data;
        this.timemark = timemark;
    }
    
    public byte[] getData() {
        return data;
    }
    public long getIoDelay() {
        return timemark;
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeLong(timemark);
        out.writeInt(data.length);
        out.write(data, 0, data.length);
    }
    
    public static ChannelDataChunk readFromStream(DataInputStream in)  throws IOException {
        long delay = in.readLong();
        byte[] dt = new byte[in.readInt()];
        in.readFully(dt);

        return new ChannelDataChunk(dt, delay);
    }
   
}
