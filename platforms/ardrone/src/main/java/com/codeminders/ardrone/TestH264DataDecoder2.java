// package com.codeminders.ardrone.decoder;

// import java.io.InputStream;
// import java.io.DataInputStream;
// import java.util.Arrays;
// import java.util.logging.Level;
// import java.util.logging.Logger;

// import com.codeminders.ardrone.ARDrone;
// import com.codeminders.ardrone.VideoDataDecoder;
// import com.twilight.h264.decoder.AVFrame;
// import com.twilight.h264.decoder.AVPacket;
// import com.twilight.h264.decoder.H264Decoder;
// import com.twilight.h264.decoder.MpegEncContext;
// import com.twilight.h264.player.FrameUtils;

// public class TestH264DataDecoder2 extends VideoDataDecoder {

//     Logger  log = Logger.getLogger(this.getClass().getName());
    
//     public static final int INBUF_SIZE = 65535;
    
//     H264Decoder codec;
//     MpegEncContext c = null;
//     int frame, len;
//     int[] got_picture = new int[1];

//     boolean done=false;
    
//     AVFrame picture;
    
//     byte[] inbuf = new byte[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//     int[] inbuf_int = new int[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//     byte[] buf = new byte[1024];
//     private int[] buffer = null;
    
//     AVPacket avpkt;
    
//     int dataPointer;
    
    
//     public TestH264DataDecoder2(ARDrone drone) {
//         super(drone);

//         avpkt = new AVPacket();
//         avpkt.av_init_packet();
        
//         Arrays.fill(inbuf, INBUF_SIZE, MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE + INBUF_SIZE, (byte)0);
        
//         codec = new H264Decoder();
//         if (codec == null) {
//             System.out.println("codec not found\n");
//             System.exit(1);
//         } 

//         c = MpegEncContext.avcodec_alloc_context();
//         picture= AVFrame.avcodec_alloc_frame();

//         if((codec.capabilities & H264Decoder.CODEC_CAP_TRUNCATED)!=0)
//             c.flags |= MpegEncContext.CODEC_FLAG_TRUNCATED; /* we do not send complete frames */

//         if (c.avcodec_open(codec) < 0) {
//             System.out.println("could not open codec\n");
//             System.exit(1);
//         }
//     }

//     public void skip(DataInputStream s, int n) throws java.io.IOException, java.lang.InterruptedException {
//         int left = n - s.skipBytes(n);
//         while( left > 0){ left -= s.skipBytes(left); Thread.sleep(5); }
//     }
//     public int readUntil(InputStream s) throws java.io.IOException, java.lang.InterruptedException {
//         int read = s.read();
//         while( read == -1){ read = s.read(); Thread.sleep(5); }
//         return read;
//     }

//     public int readHeader(DataInputStream fin) throws java.io.IOException, java.lang.InterruptedException {
//         int[] cacheRead = new int[3];
//         byte[] buf4 = new byte[4];
//         byte[] buf2 = new byte[2];

//         cacheRead[0] = readUntil(fin);
//         cacheRead[1] = readUntil(fin);
//         cacheRead[2] = readUntil(fin);
        
//         while(!(cacheRead[0] == 'P' &&
//              cacheRead[1] == 'a' &&
//              cacheRead[2] == 'V' )) {
//             cacheRead[0] = cacheRead[1];
//             cacheRead[1] = cacheRead[2];
//             cacheRead[2] = fin.read();
//         }
//         skip(fin,3);  //E version codec
//         fin.read(buf2);
//         fin.read(buf4);
//         //skip(fin,8);
//         int paveSize = little2bigs(buf2);
//         int dataSize = little2big(buf4);
//         System.out.println("paveSize: "+paveSize+" datasize: "+dataSize);
//         skip(fin, paveSize - 12);

//         return dataSize;
//     }

//     private int little2big(byte[ ] b) {
//         return ((b[3]&0xff)<<24)+((b[2]&0xff)<<16)+((b[1]&0xff)<<8)+(b[0]&0xff);
//     }
//     private int little2bigs(byte[ ] b) {
//         return ((b[1]&0xff)<<8)+(b[0]&0xff);
//     }

//     @Override
//     public void run() {
        
//         super.run();
//         //InputStream fin = getDataReader().getDataStream();
//         try {
//             int size=0;
//             DataInputStream fin = new DataInputStream(getDataReader().getDataStream());

//             while(!done){
//                 pauseCheck();
//                 try{
                    
//                     size = 15000; readHeader(fin);
//                     int read = 0;
//                     int left = size;
//                     while( left > 0){
//                         int r = fin.read(inbuf,read,left);
//                         if( r > 0){
//                             read += r;
//                             left = size - read;
//                         }
//                     }
//                     for( int i=0; i < size; i++){ inbuf_int[i] = inbuf[i]; }

//                     avpkt.size = size;
//                     avpkt.data_base = inbuf_int;
//                     avpkt.data_offset = 0;

//                     try {
//                         while (avpkt.size > 0) {
//                             len = c.avcodec_decode_video2(picture, got_picture, avpkt);
//                             if (len < 0) {
//                                 System.out.println("Error while decoding frame "+ frame);
//                                 // Discard current packet and proceed to next packet
//                                 break;
//                             } // if
//                             if (got_picture[0]!=0) {
//                                 picture = c.priv_data.displayPicture;
            
//                                 int bufferSize = picture.imageWidth * picture.imageHeight;
//                                 if (buffer == null || bufferSize != buffer.length) {
//                                     buffer = new int[bufferSize];
//                                 }
//                                 FrameUtils.YUV2RGB(picture, buffer);       
                                
//                                 notifyDroneWithDecodedFrame(0, 0, picture.imageWidth ,picture.imageHeight, buffer, 0,  picture.imageWidth); 
//                                 frame++;                    
//                             }
//                             avpkt.size -= len;
//                             avpkt.data_offset += len;
//                         }
//                     } catch(Exception ie) {
//                         // Any exception, we should try to proceed reading next packet!
//                         log.log(Level.FINEST, "Error decodeing frame", ie);
//                     } // try
//                 }catch(Exception ie) {
//                     // Any exception, we should try to proceed reading next packet!
//                     System.out.println(ie);
//                 } // try
                
//             } // while
                    
    
//         } catch(Exception ex) {
//             log.log(Level.FINEST, "Error in decoder initialization", ex);
//         }  


//     }

//     @Override
//     public void finish() {
//         done = true;
//         c.avcodec_close();
//     }

// }
