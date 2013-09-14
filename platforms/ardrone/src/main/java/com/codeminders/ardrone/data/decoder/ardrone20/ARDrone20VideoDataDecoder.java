package com.codeminders.ardrone.data.decoder.ardrone20;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.VideoDataDecoder;
import com.codeminders.ardrone.data.ARDroneDataReader;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.video.IConverter;
import com.xuggle.xuggler.video.ConverterFactory;

/*
		Modified 2013, Tim Wood
		retrofit to JavaDrone based on:

   ARDroneForP5
   https://github.com/shigeodayo/ARDroneForP5
   Copyright (C) 2013, Shigeo YOSHIDA.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

public class ARDrone20VideoDataDecoder extends VideoDataDecoder {
    
    private Logger               log  = Logger.getLogger(this.getClass().getName());
    
    private boolean              done = false;
  
    
    public ARDrone20VideoDataDecoder(ARDrone drone) {
        super(drone);
        
        setName("ARDrone 2.0 Video decoding thread");
    }

    @Override
    public void run() {
        
        // Create a Xuggler container object
		IContainer container = IContainer.make();
		IConverter converter = null;

		// Open up the container
		try {
			if (container.open(getDataReader().getDataStream(), null) < 0)
				throw new IllegalArgumentException("could not open inpustream");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// query how many streams the call to open found
		int numStreams = container.getNumStreams();

		// and iterate through the streams to find the first video stream
		int videoStreamId = -1;
		IStreamCoder videoCoder = null;
		for (int i = 0; i < numStreams; i++) {
			// Find the stream object
			IStream stream = container.getStream(i);
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}
		if (videoStreamId == -1)
			throw new RuntimeException("could not find video stream");

		/*
		 * Now we have found the video stream in this file. Let's open up our
		 * decoder so it can do work.
		 */
		if (videoCoder.open() < 0)
			throw new RuntimeException(
					"could not open video decoder for container");

		// causes additional latency!!!???
		// videoCoder.setProperty("vprofile", "baseline");
		// videoCoder.setProperty("tune", "zerolatency");


		IVideoResampler resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			// if this stream is not in BGR24, we're going to need to
			// convert it. The VideoResampler does that for us.
			resampler = IVideoResampler.make(videoCoder.getWidth(),
					videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(),
					videoCoder.getPixelType());
			if (resampler == null)
				throw new RuntimeException(
						"could not create color space resampler.");
		}

		/*
		 * Now, we start walking through the container looking at each packet.
		 */
		IPacket packet = IPacket.make();
		long firstTimestampInStream = Global.NO_PTS;
		long systemClockStartTime = 0;
		try{
		while (container.readNextPacket(packet) >= 0) {
			/*
			 * Now we have a packet, let's see if it belongs to our video stream
			 */
			if (packet.getStreamIndex() == videoStreamId) {
				/*
				 * We allocate a new picture to get the data out of Xuggler
				 */
				IVideoPicture picture = IVideoPicture.make(
						videoCoder.getPixelType(), videoCoder.getWidth(),
						videoCoder.getHeight());

				try {
					int offset = 0;
					while (offset < packet.getSize()) {
						// System.out.println("VideoManager.decode(): decode one image");
						/*
						 * Now, we decode the video, checking for any errors.
						 */
						int bytesDecoded = videoCoder.decodeVideo(picture,
								packet, offset);
						if (bytesDecoded < 0)
							throw new RuntimeException(
									"got error decoding video");
						offset += bytesDecoded;

						/*
						 * Some decoders will consume data in a packet, but will
						 * not be able to construct a full video picture yet.
						 * Therefore you should always check if you got a
						 * complete picture from the decoder
						 */
						if (picture.isComplete()) {
							// System.out.println("VideoManager.decode(): image complete");
							IVideoPicture newPic = picture;
							/*
							 * If the resampler is not null, that means we
							 * didn't get the video in BGR24 format and need to
							 * convert it into BGR24 format.
							 */
							if (resampler != null) {
								// we must resample
								newPic = IVideoPicture
										.make(resampler.getOutputPixelFormat(),
												picture.getWidth(),
												picture.getHeight());
								if (resampler.resample(newPic, picture) < 0)
									throw new RuntimeException(
											"could not resample video");
							}
							if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
								throw new RuntimeException(
										"could not decode video as BGR 24 bit data");

							/**
							 * We could just display the images as quickly as we
							 * decode them, but it turns out we can decode a lot
							 * faster than you think.
							 * 
							 * So instead, the following code does a poor-man's
							 * version of trying to match up the frame-rate
							 * requested for each IVideoPicture with the system
							 * clock time on your computer.
							 * 
							 * Remember that all Xuggler IAudioSamples and
							 * IVideoPicture objects always give timestamps in
							 * Microseconds, relative to the first decoded item.
							 * If instead you used the packet timestamps, they
							 * can be in different units depending on your
							 * IContainer, and IStream and things can get hairy
							 * quickly.
							 */
							if (firstTimestampInStream == Global.NO_PTS) {
								// This is our first time through
								firstTimestampInStream = picture.getTimeStamp();
								// get the starting clock time so we can hold up
								// frames until the right time.
								systemClockStartTime = System
										.currentTimeMillis();
							} else {
								long systemClockCurrentTime = System
										.currentTimeMillis();
								long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime
										- systemClockStartTime;

								// compute how long for this frame since the
								// first frame in the stream.
								// remember that IVideoPicture and IAudioSamples
								// timestamps are always in MICROSECONDS,
								// so we divide by 1000 to get milliseconds.
								long millisecondsStreamTimeSinceStartOfVideo = (picture
										.getTimeStamp() - firstTimestampInStream) / 1000;
								final long millisecondsTolerance = 50; // and we
																		// give
																		// ourselfs
																		// 50 ms
																		// of
																		// tolerance
								final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo - (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
								if (millisecondsToSleep > 0) {
									try {
										Thread.sleep(millisecondsToSleep);
									} catch (InterruptedException e) {
										// we might get this when the user
										// closes the dialog box, so just return
										// from the method.
										return;
									}
								}
							}

							// And finally, convert the BGR24 to an Java
							// buffered image
							if(converter == null) converter = ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24, newPic);

							BufferedImage bi = converter.toImage(newPic);

							int[] buf = bi.getRGB(0, 0, bi.getWidth() ,bi.getHeight(), null, 0,  bi.getWidth()); 

                            notifyDroneWithDecodedFrame(0, 0, bi.getWidth() ,bi.getHeight(), buf, 0,  bi.getWidth());                     

						}
					} // end of while
				} catch (Exception exc) {
					System.out.println(exc);
					// exc.printStackTrace();
				}
			} else {
				/*
				 * This packet isn't part of our video stream, so we just
				 * silently drop it.
				 */
				do {
				} while (false);
			}

		}} catch( Exception e){
			try {
                getDataReader().reconnect();
            } catch (IOException e1) {
                log.log(Level.SEVERE, " Error reconnecting video data reader", e);
            }
		}
		/*
		 * Technically since we're exiting anyway, these will be cleaned up by
		 * the garbage collector... but because we're nice people and want to be
		 * invited places for Christmas, we're going to show how to clean up.
		 */
		if (videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if (container != null) {
			container.close();
			container = null;
		}
    }

    @Override
    public void finish() {
        done = true; 
    }
}

