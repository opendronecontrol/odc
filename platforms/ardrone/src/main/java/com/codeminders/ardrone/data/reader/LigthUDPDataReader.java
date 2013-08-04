package com.codeminders.ardrone.data.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.codeminders.ardrone.data.ARDroneDataReader;

public class LigthUDPDataReader implements ARDroneDataReader {
    
    private int                timeout;
    
    private int                data_port;
    
    static final byte[]        TRIGGER_BYTES = { 0x01, 0x00, 0x00, 0x00 };

    protected DatagramSocket   socket;
    private DatagramPacket     trigger_packet;
  
    private InetAddress        drone_addr;
    
    public LigthUDPDataReader(InetAddress drone_addr, int data_port, int timeout) throws IOException {
       
        super();
        this.data_port = data_port;
        this.timeout = timeout;
        this.drone_addr = drone_addr;
        
        trigger_packet = new DatagramPacket(TRIGGER_BYTES, TRIGGER_BYTES.length, drone_addr, data_port);

        connect();
    }
    
    public InputStream getDataStream() {
        return null;
    }

    public void connect() throws IOException {
        disconnect();
        socket = new DatagramSocket(); //data_port);
        socket.setSoTimeout(timeout);
    }

    private void disconnect() {
        
       if (null != socket && socket.isConnected()) { 
           socket.disconnect();
       }
        
       if (null != socket && !socket.isClosed()) {
           socket.close();
       }
        
    }

    public int readDataBlock(byte[] buf) throws IOException {
        //send trigger data
        socket.send(trigger_packet);
        //receive data
        DatagramPacket packet = new DatagramPacket(buf, buf.length, drone_addr, data_port);
        socket.receive(packet);

        return packet.getLength();
    }
    
    public synchronized void finish()
    {  
        disconnect();
    }
    
    @Override
    public boolean isStreamSupported() {
        return false;
    }

    @Override
    public void reconnect() throws IOException {
        disconnect();
        connect(); 
    }
}
