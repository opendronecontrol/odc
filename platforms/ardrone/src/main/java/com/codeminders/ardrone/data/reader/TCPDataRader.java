package com.codeminders.ardrone.data.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.codeminders.ardrone.data.ARDroneDataReader;


public class TCPDataRader  implements ARDroneDataReader  {
    
    Logger                     log = Logger.getLogger(this.getClass().getName());
    
    private InetAddress        drone_addr;
    private int                data_port;
    private int                timeout;

    private Socket             socket;
    
    private InputStream        socketInput;
    
    public TCPDataRader(InetAddress drone_addr, int data_port, int timeout) throws IOException {
        super();
        
        this.drone_addr = drone_addr;
        this.data_port = data_port;
        this.timeout = timeout;
        
        connect();
    }
    
    private void connect() throws IOException {
        
        socket = new Socket(drone_addr, data_port);
        socket.setSoTimeout(timeout);
        socketInput = socket.getInputStream();
    }

    @Override
    public InputStream getDataStream() {
        return socketInput;
    }

    @Override
    public void finish() {
        disconnect();
    }

    private void disconnect() {
        if (socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
               log.log(Level.FINER, "Excepton on stopping TCP reading", e);
            }
        }
    }

    @Override
    public int readDataBlock(byte[] buf) throws IOException {
        return socketInput.read(buf);
    }

    @Override
    public boolean isStreamSupported() {
        return true;
    }

    @Override
    public void reconnect() throws IOException {
        disconnect();
        connect();
    }
}
