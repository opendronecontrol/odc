// package com.codeminders.ardrone.data.decoder.ardrone20;

// import java.io.IOException;
// import java.util.logging.Level;
// import java.util.logging.Logger;

// import com.codeminders.ardrone.ARDrone;
// import com.codeminders.ardrone.VideoDataDecoder;
// import com.codeminders.ardrone.data.ARDroneDataReader;

// import com.fishuyo.ardrone20.PaveVideoDecode;

// public class ARDrone20VideoDataDecoder extends VideoDataDecoder {
    
//     private Logger               log  = Logger.getLogger(this.getClass().getName());
    
//     final PaveVideoDecode     vi   = new PaveVideoDecode();
    
//     private boolean              done = false;
  
//     byte[]                       buffer; 

//     int c=0;
    
//     public ARDrone20VideoDataDecoder(ARDrone drone, int buffer_size) {
//         super(drone);
//         buffer = new byte[buffer_size];
        
//         setName("ARDrone 2.0 Video decoding thread");
//     }

//     @Override
//     public void run() {
        
//         super.run();
//         ARDroneDataReader reader = getDataReader();
//         int len = 0;
//         while (!done) {
//             try {
//                 pauseCheck();
//                 len = reader.readDataBlock(buffer);
//                 if (len > 0) {
//                     vi.addImageStream(buffer, len);
//                     notifyDroneWithDecodedFrame(0, 0, vi.getWidth(), vi.getHeight(), vi.getJavaPixelData(), 0, vi.getWidth());
//                     // java.io.FileOutputStream out = new java.io.FileOutputStream("/Users/fishuyo/vpacket" + c);
//                     // out.write(buffer);
//                     // c += 1;
//                 }
//             } catch (IOException e) {
//                 log.log(Level.SEVERE, " Error reading data from data input stream. Stopping decoding thread", e);
//                 try {
//                     reader.reconnect();
//                 } catch (IOException e1) {
//                     log.log(Level.SEVERE, " Error reconnecting data reader", e);
//                 }
//             }
//         } 
        
//         log.fine("Video Decodding thread is stopped");
//     }

//     @Override
//     public void finish() {
//         done = true; 
//     }
// }
