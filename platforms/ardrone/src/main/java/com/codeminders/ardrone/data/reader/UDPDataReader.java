package com.codeminders.ardrone.data.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import com.codeminders.ardrone.data.ARDroneDataReader;

public class UDPDataReader implements ARDroneDataReader {
	
    private int timeout;
    
    protected DatagramChannel  channel;
    protected Selector         selector;
    
    private InetAddress        drone_addr;
	private int                data_port;
    
	private int                buffer_size;
	
	static final byte[]        TRIGGER_BYTES = { 0x01, 0x00, 0x00, 0x00 };
	
	ByteBuffer                 trigger_buffer = ByteBuffer.allocate(TRIGGER_BYTES.length);
	ByteBuffer                 inbuf = ByteBuffer.allocate(buffer_size);
    
    public UDPDataReader(InetAddress drone_addr, int data_port, int timeout) throws ClosedChannelException, IOException {
        super();
        this.drone_addr = drone_addr;
        this.data_port = data_port;

        this.timeout = timeout;
        
        trigger_buffer.put(TRIGGER_BYTES);
        trigger_buffer.flip();
        
        connect();
    }

    private void connect() throws IOException, ClosedChannelException {
        
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(data_port));
        channel.connect(new InetSocketAddress(drone_addr, data_port));

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void disconnect() {
        try {
            if (selector.isOpen())
                selector.close();
        } catch (IOException iox) 
        {
            // ignore
        }
        
        if (!channel.socket().isClosed()) {
            channel.socket().close();
        }

        try {
            if (channel.isConnected())
                channel.disconnect();
        } catch (IOException iox) 
        {
           // ignore
        }
        
        try {
            channel.close();
        } catch (IOException iox) {
            // ignore
        }
        
    }
    
    @Override
    public int readDataBlock(byte[] buf) throws IOException
    {
        int len = 0;
        selector.select(timeout);
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();

            if (key.isWritable()) {
                channel.write(trigger_buffer);
                channel.register(selector, SelectionKey.OP_READ);
                // prepare buffer for new reconnection attempt
                trigger_buffer.clear();
                trigger_buffer.put(TRIGGER_BYTES);
                trigger_buffer.flip();
            } else if (key.isReadable()) {
                return channel.read(ByteBuffer.wrap(buf));
            }
        }
        
        return len;
    }

    public synchronized void finish()
    {  
        if (null != selector) {
            selector.wakeup();
        }
    }

    @Override
    public InputStream getDataStream() {
        return null;
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