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

// public class TestH264DataDecoder extends VideoDataDecoder {

//     Logger  log = Logger.getLogger(this.getClass().getName());
    
//     public static final int INBUF_SIZE = 65535;
    
//     H264Decoder codec;
//     MpegEncContext c = null;
//     int frame, len;
//     int[] got_picture = new int[1];
    
//     AVFrame picture;
    
//     byte[] inbuf = new byte[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//     int[] inbuf_int = new int[INBUF_SIZE + MpegEncContext.FF_INPUT_BUFFER_PADDING_SIZE];
//     byte[] buf = new byte[1024];
//     private int[] buffer = null;
    
//     AVPacket avpkt;
    
//     int dataPointer;
    
    
//     public TestH264DataDecoder(ARDrone drone) {
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

//     @Override
//     public void run() {
        
//         //InputStream fin = getDataReader().getDataStream();
//         DataInputStream fin = new DataInputStream(getDataReader().getDataStream());
//         try {
//             // avpkt must contain exactly 1 NAL Unit in order for decoder to decode correctly.
//             // thus we must read until we get next NAL header before sending it to decoder.
//             // Find 1st NAL
//             int[] cacheRead = new int[3];
//             cacheRead[0] = fin.read();
//             cacheRead[1] = fin.read();
//             cacheRead[2] = fin.read();
            
//             while(!(
//                     cacheRead[0] == 'a' &&
//                     cacheRead[1] == 'V' &&
//                     cacheRead[2] == 'E' 
//                     )) {
//                  cacheRead[0] = cacheRead[1];
//                  cacheRead[1] = cacheRead[2];
//                  cacheRead[2] = fin.read();
//             } // while

//             fin.skipBytes(2);
//             int paveSize = fin.readShort();
//             int dataSize = fin.readInt();
//             fin.skipBytes(paveSize - 12 + 4);
            
//             boolean hasMoreNAL = true;
            
//             // 4 first bytes always indicate NAL header
//             inbuf_int[0]=inbuf_int[1]=inbuf_int[2]=0x00;
//             inbuf_int[3]=0x01;
            
//             while(hasMoreNAL) { // TODO: Possible error because we use not file 
//                 dataPointer = 4;
//                 // Find next NAL
//                 cacheRead[0] = fin.read();
//                 if(cacheRead[0]==-1) hasMoreNAL = false;
//                 cacheRead[1] = fin.read();
//                 if(cacheRead[1]==-1) hasMoreNAL = false;
//                 cacheRead[2] = fin.read();
//                 if(cacheRead[2]==-1) hasMoreNAL = false;
//                 while(!(
//                         cacheRead[0] == 'P' &&
//                         cacheRead[1] == 'a' &&
//                         cacheRead[2] == 'V' 
//                         ) && hasMoreNAL) {
//                      inbuf_int[dataPointer++] = cacheRead[0];
//                      cacheRead[0] = cacheRead[1];
//                      cacheRead[1] = cacheRead[2];
//                      cacheRead[2] = fin.read();
//                     if(cacheRead[2]==-1) hasMoreNAL = false;
//                 } // while

//                 avpkt.size = dataPointer;

//                 avpkt.data_base = inbuf_int;
//                 avpkt.data_offset = 0;

//                 try {
//                     while (avpkt.size > 0) {
//                         len = c.avcodec_decode_video2(picture, got_picture, avpkt);
//                         if (len < 0) {
//                             System.out.println("Error while decoding frame "+ frame);
//                             // Discard current packet and proceed to next packet
//                             break;
//                         } // if
//                         if (got_picture[0]!=0) {
//                             picture = c.priv_data.displayPicture;
        
//                             int bufferSize = picture.imageWidth * picture.imageHeight;
//                             if (buffer == null || bufferSize != buffer.length) {
//                                 buffer = new int[bufferSize];
//                             }
//                             FrameUtils.YUV2RGB(picture, buffer);       
                            
//                             notifyDroneWithDecodedFrame(0, 0, picture.imageWidth ,picture.imageHeight, buffer, 0,  picture.imageWidth);                     
//                             frame++;
//                         }
//                         avpkt.size -= len;
//                         avpkt.data_offset += len;
//                     }
//                 } catch(Exception ie) {
//                     // Any exception, we should try to proceed reading next packet!
//                     log.log(Level.FINEST, "Error decodeing frame", ie);
//                 } // try
                
//             } // while
                    
    
//         } catch(Exception ex) {
//             log.log(Level.FINEST, "Error in decoder initialization", ex);
//         }  


//     }

//     @Override
//     public void finish() {
//         c.avcodec_close();
//     }

// }
