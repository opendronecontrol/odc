package com.codeminders.ardrone.version.ftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.codeminders.ardrone.version.DroneVersionReader;

public class DroneFTPversionReader implements DroneVersionReader {

    private Logger                   log = Logger.getLogger(getClass().getName());
    
    private static final int         FTP_PORT = 5551;
    private static final String      VERSION_FILE_NAME = "version.txt";
    
    String                           ftpVersionFileLocation; 
    
    public DroneFTPversionReader(InetAddress drone_addr) {
        this.ftpVersionFileLocation = "ftp://"+drone_addr.getHostAddress() + ":" + FTP_PORT + "/" + VERSION_FILE_NAME;
    }

    @Override
    public String readDroneVersion() throws IOException {

        InputStream is = null;
        ByteArrayOutputStream  bos = null;
        try {        
            // log.info("Attempting to read AR Drone version using FTP. Version file is: "+ ftpVersionFileLocation);
            URL url = new URL(ftpVersionFileLocation);
            URLConnection ftpConnection = url.openConnection();
            ftpConnection.setConnectTimeout(1000*5);
            // log.info(ftpVersionFileLocation + "- Connection Opened");
            
            is = ftpConnection.getInputStream();
            bos = new ByteArrayOutputStream();
    
            byte[] buffer = new byte[1024];
            int readCount;
    
            while((readCount = is.read(buffer)) > 0)
            {
              bos.write(buffer, 0, readCount);
            }
            
            return bos.toString();
        } finally {
            if (null != bos) {
                bos.close();
            }
            if (null != is) {
                is.close ();
            }
        }         
    }

}
