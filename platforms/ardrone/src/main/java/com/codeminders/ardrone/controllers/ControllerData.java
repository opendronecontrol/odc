package com.codeminders.ardrone.controllers;

public class ControllerData {
    private byte[]           buf;
    int                      actualBufferDataLength;
    
    public ControllerData(byte[] buf, int actualBufferDataLength) {
        super();
        this.buf = buf;
        this.actualBufferDataLength = actualBufferDataLength;
    }
    public byte[] getBuffer() {
        return buf;
    }
    public int getActualBufferDataLength() {
        return actualBufferDataLength;
    }
}
