package com.codeminders.ardrone.data.decoder.ardrone10.video;


// Copyright (C) 2007-2011, PARROT SA, all rights reserved.

// DISCLAIMER
// The APIs is provided by PARROT and contributors "AS IS" and any express or
// implied warranties, including, but not limited to, the implied warranties of
// merchantability
// and fitness for a particular purpose are disclaimed. In no event shall PARROT
// and contributors be liable for any direct, indirect, incidental, special,
// exemplary, or
// consequential damages (including, but not limited to, procurement of
// substitute goods or services; loss of use, data, or profits; or business
// interruption) however
// caused and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of the use
// of this software, even if advised of the possibility of such damage.

// Author : Daniel Schmidt
// Publishing date : 2011-07-15
// based on work by : Wilke Jansoone

// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// - Redistributions of source code must retain the above copyright notice, this
// list of conditions, the disclaimer and the original author of the source
// code.
// - Neither the name of the PixVillage Team, nor the names of its contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission.

public class BufferedVideoImage
{
    private static final int BLOCK_WIDTH = 8;
    private static final int CIF_WIDTH = 88;
    private static final int CIG_HEIGHT = 72;

    private static final int VGA_WIDTH = 160;
    private static final int VGA_HEIGHT = 120;

    private static final int TABLE_QUANTIZATION_MODE = 31;

    private static final int FIX_0_298631336 = 2446;
    private static final int FIX_0_390180644 = 3196;
    private static final int FIX_0_541196100 = 4433;
    private static final int FIX_0_765366865 = 6270;
    private static final int FIX_0_899976223 = 7373;
    private static final int FIX_1_175875602 = 9633;
    private static final int FIX_1_501321110 = 12299;
    private static final int FIX_1_847759065 = 15137;
    private static final int FIX_1_961570560 = 16069;
    private static final int FIX_2_053119869 = 16819;
    private static final int FIX_2_562915447 = 20995;
    private static final int FIX_3_072711026 = 25172;

    private static final int BITS = 13;
    private static final int PASS1_BITS = 1;
    private static final int F1 = BITS - PASS1_BITS - 1;
    private static final int F2 = BITS - PASS1_BITS;
    private static final int F3 = BITS + PASS1_BITS + 3;

    /**
     * 176px x 144px
     */
    private static final int CIF = 1;

    /**
     * 320px x 240px
     */
    private static final int QVGA = 2;

    private static final short[] ZIGZAG_POSITIONS = new short[] { 0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14, 21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63, };

    // Cfr. Handbook of Data Compression - Page 529
    // David Salomon
    // Giovanni Motta

    private static final short[] QUANTIZER_VALUES = new short[] { 3, 5, 7, 9, 11, 13, 15, 17, 5, 7, 9, 11, 13, 15, 17, 19, 7, 9, 11, 13, 15, 17, 19, 21, 9, 11, 13, 15, 17, 19, 21, 23, 11, 13, 15, 17, 19, 21, 23, 25, 13, 15, 17, 19, 21, 23, 25, 27, 15, 17, 19, 21, 23, 25, 27, 29, 17, 19, 21, 23, 25, 27, 29, 31 };

    static byte[] CLZLUT = new byte[] { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private static final int[] CROMA_QUADRANT_OFFSETS = new int[] { 0, 4, 32, 36 };

    private short[] dataBlockBuffer = new short[64];

    private int streamField;
    private int streamFieldBitIndex;
    private int streamIndex;
    private int sliceCount;
    private boolean pictureComplete;
    private int pictureFormat;
    private int resolution;
    private int pictureType;
    private int quantizerMode;
    private int frameIndex;
    private int sliceIndex;
    private int blockCount;
    private int width;
    private int height;

    /**
     * Length of one row of pixels in the destination image in bytes.
     */
    private int pixelRowSize;
    private byte[] imageStreamByteArray;
    private int imageStreamCapacity;
    private ImageSlice imageSlice;
    private int[] javaPixelData;

    /* Data used by inverseTransform */
    private int[] workSpace = new int[64];
    
    /* Data used by decodeFieldBytes */
    private int run;
    private int level;
    private boolean last;
    
    /*
     * Convert a stream to an image
     * 
     * Takes in bytes representing an image and renders the image after decoding the bytes.
     * 
     * @param ByteBuffer stream
     *      A ByteBuffer full of the bytes that represent the image to be decoded.
     */
    public void addImageStream(byte[] imageStreamByteArray, int actualDatalength)
    {
        this.imageStreamByteArray = imageStreamByteArray;
        imageStreamCapacity = actualDatalength;
        processStream();
    }
    
    /*
     * Adjusts the stream to fix the start of the actual data
     * 
     * Prepares the stream data for reading the header, by adjusting byte values
     */
    private void alignStreamData()
    {
        int alignedLength;
        int actualLength;

        actualLength = streamFieldBitIndex;

        if (actualLength > 0)
        {
            alignedLength = (actualLength & ~7);
            if (alignedLength != actualLength)
            {
                alignedLength += 0x08;
                streamField <<= (alignedLength - actualLength);
                streamFieldBitIndex = alignedLength;
            }
        }
    }
    
    /*
     * Constructs the image from byte values
     * 
     * From the blocks in the image, uses the byte data which is converted to rgb
     * and applies various transformations, like saturation, to adjust for the
     * image creation. See comments above peekStreamData for more information on
     * how the image bytes are laid out and decoded then put together to form the 
     * image slice.
     */
    private void composeImageSlice()
    {
        int u, ug, ub;
        int v, vg, vr;
        int r, g, b;

        int lumaElementIndex1 = 0;
        int lumaElementIndex2 = 0;
        int chromaOffset = 0;

        int dataIndex1 = 0;
        int dataIndex2 = 0;

        int lumaElementValue1 = 0;
        int lumaElementValue2 = 0;
        int chromaBlueValue = 0;
        int chromaRedValue = 0;

        int x = 0;

        int[] pixelDataQuadrantOffsets = new int[] { 0, BLOCK_WIDTH, width * BLOCK_WIDTH, (width * BLOCK_WIDTH) + BLOCK_WIDTH };
        short[] mbDBArr;
        short[][] dbArr;
        int imageDataOffset = (sliceIndex - 1) * width * 16;

        for (MacroBlock macroBlock : imageSlice.MacroBlocks)
        {
            dbArr = macroBlock.DataBlocks;
            for (int verticalStep = 0; verticalStep < BLOCK_WIDTH / 2; verticalStep++)
            {
                chromaOffset = verticalStep * BLOCK_WIDTH;
                lumaElementIndex1 = verticalStep * BLOCK_WIDTH * 2;
                lumaElementIndex2 = lumaElementIndex1 + BLOCK_WIDTH;

                dataIndex1 = imageDataOffset + (2 * verticalStep * width);
                dataIndex2 = dataIndex1 + width;

                for (int horizontalStep = 0; horizontalStep < BLOCK_WIDTH / 2; horizontalStep++)
                {
                    for (int quadrant = 0; quadrant < 4; quadrant++)
                    {
                        int uvg;
                        int chromaIndex = chromaOffset + CROMA_QUADRANT_OFFSETS[quadrant] + horizontalStep;
                        chromaBlueValue = dbArr[4][chromaIndex];
                        chromaRedValue = dbArr[5][chromaIndex];
                        mbDBArr = dbArr[quadrant];

                        u = chromaBlueValue - 128;
                        ug = 88 * u;
                        ub = 454 * u;

                        v = chromaRedValue - 128;
                        vg = 183 * v;
                        vr = 359 * v;

                        uvg = ug + vg;
                        
                        for (int pixel = 0; pixel < 2; pixel++)
                        {
                            int deltaIndex = 2 * horizontalStep + pixel;
                            lumaElementValue1 = mbDBArr[lumaElementIndex1 + deltaIndex] << 8;
                            lumaElementValue2 = mbDBArr[lumaElementIndex2 + deltaIndex] << 8;
                            x = lumaElementValue1 + vr;
                            if (x < 0)
                            {
                                r = 0;
                            } else
                            {
                                x >>= 8;
                                r = (x > 0xFF) ? 0xFF : x;
                            }
                            x = lumaElementValue1 - uvg;
                            if (x < 0)
                            {
                                g = 0;
                            } else
                            {
                                x >>= 8;
                                g = (x > 0xFF) ? 0xFF : x;
                            }
                            x = lumaElementValue1 + ub;
                            if (x < 0)
                            {
                                b = 0;
                            } else
                            {
                                x >>= 8;
                                b = (x > 0xFF) ? 0xFF : x;
                            }
                            javaPixelData[dataIndex1 + pixelDataQuadrantOffsets[quadrant] + deltaIndex] = ((r << 16) | (g << 8) | b);
                            
                            x = lumaElementValue2 + vr;
                            if (x < 0)
                            {
                                r = 0;
                            } else
                            {
                                x >>= 8;
                                r = (x > 0xFF) ? 0xFF : x;
                            }
                            x = lumaElementValue2 - uvg;
                            if (x < 0)
                            {
                                g = 0;
                            } else
                            {
                                x >>= 8;
                                g = (x > 0xFF) ? 0xFF : x;
                            }
                            x = lumaElementValue2 + ub;
                            if (x < 0)
                            {
                                b = 0;
                            } else
                            {
                                x >>= 8;
                                b = (x > 0xFF) ? 0xFF : x;
                            }
                            javaPixelData[dataIndex2 + pixelDataQuadrantOffsets[quadrant] + deltaIndex] = ((r << 16) | (g << 8) | b);
                        }
                    }
                }
            }

            imageDataOffset += 16;
        }
    }

    /*
     * Decompresses the byte data into a uncompressed format which can be parsed easier
     * 
     * Decodes the byte stream data by combining the two fields, run fields and level fields
     * which are used to compress data.
     * 
     * @param int[] run
     *      Wrapper for an int. Used to calculate run value
     * 
     * @param int[] level
     *      Wrapper for an int. Used to calculate run value with a sign
     * 
     * @param boolean[]last
     *      Wrapper for a boolean. Used to determine if this is the end of the stream or not.
     */
    private void decodeFieldBytes()
    {
        int streamCode;
        int streamLength;
        int zeroCount;
        int temp;
        int sign;

        // Use the RLE and Huffman dictionaries to understand this code
        // fragment. You can find
        // them in the developers guide on page 34.
        // The bits in the data are actually composed of two kinds of fields:
        // - run fields - this field contains information on the number of
        // consecutive zeros.
        // - level fields - this field contains the actual non zero value which
        // can be negative or positive.
        // First we extract the run field info and then the level field info.

        // NOTE: explicit inline expansion done here; simplified quite a bit
        //streamCode = peekStreamData(imageStream, 32);
        
        if ((streamFieldBitIndex > 0) && streamIndex < (imageStreamCapacity >> 2))
        {
            temp =  ((imageStreamByteArray[streamIndex * 4 + 0] & 0xFF) | 
                    ((imageStreamByteArray[streamIndex * 4 + 1] & 0xFF) << 8) | 
                    ((imageStreamByteArray[streamIndex * 4 + 2] & 0xFF) << 16) | 
                    ((imageStreamByteArray[streamIndex * 4 + 3] & 0xFF) << 24));

            streamCode =    ((streamField >>> streamFieldBitIndex) << streamFieldBitIndex) | 
                            (temp >>> (32 - streamFieldBitIndex));          
        }
        else
        {
            streamCode = streamField;
        }
        
        // Determine number of consecutive zeros in zig zag. (a.k.a
        // 'run' field info)

        // Suppose we have following bit sequence:
        // 00001111.....
        // 1 - Count the number of leading zeros -> 4
        // Coarse value lookup is thus 00001
        // 2 - Lookup the additional value, for coarse value 00001 this is 3
        // addtional bits
        // 3 - Calculate value of run, for coarse value 00001 this is (111) + 8

        zeroCount = CLZLUT[streamCode >>> 24];
        if (zeroCount == 8)
        {
            zeroCount += CLZLUT[(streamCode >>> 16) & 0xFF];
            if (zeroCount == 16)
            {
                zeroCount += CLZLUT[(streamCode >>> 8) & 0xFF];
                if (zeroCount == 24)
                {
                    zeroCount += CLZLUT[streamCode & 0xFF];
                }
            }
        }

        if (zeroCount > 1)
        {
            temp = (streamCode << (zeroCount + 1)) >>> (32 - (zeroCount - 1));
            // (2)
            // ->
            // shift right to determine the additional bits (number of
            // additional bits is zerocount -1)
            
            // NOTE: earlier operations on streamCode and streamLength have been
            // included in operations below, comments may be inaccurate
            
            streamCode <<= 2*zeroCount; // - shift all of the run bits out
                                            // of the way so the first bit
                                            // points to the first bit of the
                                            // level field
            streamLength = 2*zeroCount; // - position bit pointer to keep tack
                                            // off how many bits to consume
                                            // later on the stream
            run = temp + (1 << (zeroCount - 1)); // - (3) -> calculate run
                                                    // value
        } else
        {
            streamCode <<= (zeroCount + 1); // - (2) -> shift left to get
            // rid of the coarse value
            streamLength = zeroCount + 1; // - position bit pointer to keep track
            // off how many bits to consume later on
            // the stream.

            run = zeroCount;
        }

        // Determine non zero value. (a.k.a 'level' field info)

        // Suppose we have following bit sequence:
        // 000011111.....
        // 1 - Count the number of leading zeros -> 4
        // Coarse value lookup is thus 00001
        // 2 - Lookup the additional value, for coarse value 00001 this is 4
        // addtional bits (last bit is sign bit)
        // 3 - Calculate value of run, for coarse value 00001 this is (xxx) + 8,
        // multiply by sign

        zeroCount = CLZLUT[streamCode >>> 24];
        if (zeroCount == 8)
        {
            zeroCount += CLZLUT[(streamCode >>> 16) & 0xFF];
            if (zeroCount == 16)
            {
                zeroCount += CLZLUT[(streamCode >>> 8) & 0xFF];
                if (zeroCount == 24)
                {
                    zeroCount += CLZLUT[streamCode & 0xFF];
                }
            }
        }

        if (zeroCount == 1)
        {
            // NOTE: earlier operations on streamCode and streamLength have been
            // included in operations below, comments may be inaccurate
            
            streamCode <<= 2;   // - (1)
            streamLength += 2;  // - position bit pointer to keep track
                                // off how many bits to consume later on the stream

            // If coarse value is 01 according to the Huffman dictionary this
            // means EOB, so there is no run and level and we indicate this 
            // by setting last to true (run and level do not need any clearing 
            // here because they are ignored)
            last = true;
        } else
        {
            if (zeroCount == 0)
            {
                // NOTE: earlier operations on streamCode and streamLength have been
                // included in operations below, comments may be inaccurate
                
                streamLength += 2;                      // - position bit pointer to keep track
                                                        // off how many bits to consume later on the stream
                streamCode = (streamCode << 1) >>> 31;  // - (2) -> shift right
                
                temp = (streamCode >>> 1) + 1;          // take into account that last bit is sign, 
                                                        // so shift it out of the way
            }
            else
            {
                // NOTE: earlier operations on streamCode and streamLength have been
                // included in operations below, comments may be inaccurate
                
                streamLength += 2*zeroCount + 1;    // - position bit pointer to keep track
                                                    // off how many bits to consume later on the stream
                streamCode = (streamCode << (zeroCount + 1)) >>> (32 - zeroCount); // - (2) -> shift right

                temp = streamCode >>> 1;                // take into account that last bit is sign,
                                                        // so shift it out of the way
                temp += (int) (1 << (zeroCount - 1));   // - (3) -> calculate run value without sign
            }

            // sign = (sbyte)(streamCode & 1); // determine sign, last bit is sign
            sign = streamCode & 1; // determine sign, last bit is sign

            level = (sign == 1) ? -temp : temp; // - (3) -> calculate run
            // value with sign
            last = false;
        }

        readStreamDataInt(streamLength);
    }

    /*
     * Decodes the field bytes within this block.
     * 
     * Reads the stream 
     */
    private void getBlockBytes(boolean acCoefficientsAvailable)
    {
        int zigZagPosition = 0;
        int matrixPosition = 0;

        for (int i = 0; i < dataBlockBuffer.length; i++)
            dataBlockBuffer[i] = 0;

        int dcCoefficientTemp = readStreamDataInt(10);

        if (quantizerMode == TABLE_QUANTIZATION_MODE)
        {
            dataBlockBuffer[0] = (short) (dcCoefficientTemp * QUANTIZER_VALUES[0]);

            if (acCoefficientsAvailable)
            {
                decodeFieldBytes();

                while (!last)
                {
                    zigZagPosition += run + 1;
                    matrixPosition = ZIGZAG_POSITIONS[zigZagPosition];
                    level *= QUANTIZER_VALUES[matrixPosition];
                    dataBlockBuffer[matrixPosition] = (short) level;
                    decodeFieldBytes();
                }
            }
        } else
        {
            // Currently not implemented.
            throw new RuntimeException("ant quantizer mode is not yet implemented.");
        }
    }

    public int getFrameIndex() 
    {
        return frameIndex;
    }

    public int getHeight()
    {
        return height;
    }

    public int[] getJavaPixelData()
    {
        return javaPixelData;
    }

    public int getPictureType()
    {
        return pictureType;
    }

    public int getPixelRowSize()
    {
        return pixelRowSize;
    }

    public int getSliceCount()
    {
        return sliceCount;
    }

    public int getWidth()
    {
        return width;
    }

    void inverseTransform(int macroBlockIndex, int dataBlockIndex)
    {
        int z1, z2, z3, z4, z5;
        int tmp0, tmp1, tmp2, tmp3;
        int tmp10, tmp11, tmp12, tmp13;

        int pointer;
        
        short[] blockArray = imageSlice.MacroBlocks[macroBlockIndex].DataBlocks[dataBlockIndex];

        for (pointer = 0; pointer < 8; pointer++)
        {
            if (dataBlockBuffer[pointer + 8] == 0 && dataBlockBuffer[pointer + 16] == 0 && dataBlockBuffer[pointer + 24] == 0 && dataBlockBuffer[pointer + 32] == 0 && dataBlockBuffer[pointer + 40] == 0 && dataBlockBuffer[pointer + 48] == 0 && dataBlockBuffer[pointer + 56] == 0)
            {
                int dcValue = dataBlockBuffer[pointer] << PASS1_BITS;

                workSpace[pointer + 0] = dcValue;
                workSpace[pointer + 8] = dcValue;
                workSpace[pointer + 16] = dcValue;
                workSpace[pointer + 24] = dcValue;
                workSpace[pointer + 32] = dcValue;
                workSpace[pointer + 40] = dcValue;
                workSpace[pointer + 48] = dcValue;
                workSpace[pointer + 56] = dcValue;
            } else
            {
                z2 = dataBlockBuffer[pointer + 16];
                z3 = dataBlockBuffer[pointer + 48];

                z1 = (z2 + z3) * FIX_0_541196100;
                tmp2 = z1 + z3 * -FIX_1_847759065;
                tmp3 = z1 + z2 * FIX_0_765366865;

                z2 = dataBlockBuffer[pointer];
                z3 = dataBlockBuffer[pointer + 32];

                tmp0 = (z2 + z3) << BITS;
                tmp1 = (z2 - z3) << BITS;

                tmp10 = tmp0 + tmp3;
                tmp13 = tmp0 - tmp3;
                tmp11 = tmp1 + tmp2;
                tmp12 = tmp1 - tmp2;

                tmp0 = dataBlockBuffer[pointer + 56];
                tmp1 = dataBlockBuffer[pointer + 40];
                tmp2 = dataBlockBuffer[pointer + 24];
                tmp3 = dataBlockBuffer[pointer + 8];

                z1 = tmp0 + tmp3;
                z2 = tmp1 + tmp2;
                z3 = tmp0 + tmp2;
                z4 = tmp1 + tmp3;
                z5 = (z3 + z4) * FIX_1_175875602;

                tmp0 = tmp0 * FIX_0_298631336;
                tmp1 = tmp1 * FIX_2_053119869;
                tmp2 = tmp2 * FIX_3_072711026;
                tmp3 = tmp3 * FIX_1_501321110;
                z1 = z1 * -FIX_0_899976223;
                z2 = z2 * -FIX_2_562915447;
                z3 = z3 * -FIX_1_961570560;
                z4 = z4 * -FIX_0_390180644;

                z3 += z5;
                z4 += z5;

                tmp0 += z1 + z3;
                tmp1 += z2 + z4;
                tmp2 += z2 + z3;
                tmp3 += z1 + z4;

                workSpace[pointer + 0] = ((tmp10 + tmp3 + (1 << F1)) >> F2);
                workSpace[pointer + 56] = ((tmp10 - tmp3 + (1 << F1)) >> F2);
                workSpace[pointer + 8] = ((tmp11 + tmp2 + (1 << F1)) >> F2);
                workSpace[pointer + 48] = ((tmp11 - tmp2 + (1 << F1)) >> F2);
                workSpace[pointer + 16] = ((tmp12 + tmp1 + (1 << F1)) >> F2);
                workSpace[pointer + 40] = ((tmp12 - tmp1 + (1 << F1)) >> F2);
                workSpace[pointer + 24] = ((tmp13 + tmp0 + (1 << F1)) >> F2);
                workSpace[pointer + 32] = ((tmp13 - tmp0 + (1 << F1)) >> F2);

            }
        }

        for (pointer = 0; pointer < 64; pointer += 8)
        {
            z2 = workSpace[pointer + 2];
            z3 = workSpace[pointer + 6];

            z1 = (z2 + z3) * FIX_0_541196100;
            tmp2 = z1 + z3 * -FIX_1_847759065;
            tmp3 = z1 + z2 * FIX_0_765366865;

            z1 = workSpace[pointer];
            z2 = workSpace[pointer + 4];

            tmp0 = (z1 + z2) << BITS;
            tmp1 = (z1 - z2) << BITS;

            tmp10 = tmp0 + tmp3;
            tmp13 = tmp0 - tmp3;
            tmp11 = tmp1 + tmp2;
            tmp12 = tmp1 - tmp2;

            tmp3 = workSpace[pointer + 1];
            tmp2 = workSpace[pointer + 3];
            tmp1 = workSpace[pointer + 5];
            tmp0 = workSpace[pointer + 7];

            z1 = (tmp0 + tmp3) * -FIX_0_899976223;
            z2 = (tmp1 + tmp2) * -FIX_2_562915447;
            z3 = tmp0 + tmp2;
            z4 = tmp1 + tmp3;

            z5 = (z3 + z4) * FIX_1_175875602;

            z3 = (z3 * -FIX_1_961570560) + z5;
            z4 = (z4 * -FIX_0_390180644) + z5;

            tmp0 = (tmp0 * FIX_0_298631336) + z1 + z3;
            tmp1 = (tmp1 * FIX_2_053119869) + z2 + z4;
            tmp2 = (tmp2 * FIX_3_072711026) + z2 + z3;
            tmp3 = (tmp3 * FIX_1_501321110) + z1 + z4;
            
            blockArray[pointer] = (short) ((tmp10 + tmp3) >> F3);
            blockArray[pointer + 1] = (short) ((tmp11 + tmp2) >> F3);
            blockArray[pointer + 2] = (short) ((tmp12 + tmp1) >> F3);
            blockArray[pointer + 3] = (short) ((tmp13 + tmp0) >> F3);
            blockArray[pointer + 4] = (short) ((tmp13 - tmp0) >> F3);
            blockArray[pointer + 5] = (short) ((tmp12 - tmp1) >> F3);
            blockArray[pointer + 6] = (short) ((tmp11 - tmp2) >> F3);
            blockArray[pointer + 7] = (short) ((tmp10 - tmp3) >> F3);
        }
    }

    // Blockline:
    // _______
    // | 1 | 2 |
    // |___|___| Y
    // | 3 | 4 |
    // |___|___|
    // ___
    // | 5 |
    // |___| Cb
    // ___
    // | 6 |
    // |___| Cr
    //
    // Layout in memory
    // _______________________
    // | 1 | 2 | 3 | 4 | 5 | 6 | ...
    // |___|___|___|___|___|___|
    //

    // Example, suppose the six data sub blocks are as follows:

    // ==============Y0============== ==============Y1==============
    // ==============Y2============== ==============Y3==============

    // 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7,
    // 0, 1, 2, 3, 4, 5, 6, 7,
    // 8, 9, 10, 11, 12, 13, 14, 15, 8, 9, 10, 11, 12, 13, 14, 15, 8, 9, 10, 11,
    // 12, 13, 14, 15, 8, 9, 10, 11, 12, 13, 14, 15,
    // 16, 17, 18, 19, 20, 21, 22, 23, 16, 17, 18, 19, 20, 21, 22, 23, 16, 17,
    // 18, 19, 20, 21, 22, 23, 16, 17, 18, 19, 20, 21, 22, 23,
    // 24, 25, 26, 27, 28, 29, 30, 31, 24, 25, 26, 27, 28, 29, 30, 31, 24, 25,
    // 26, 27, 28, 29, 30, 31, 24, 25, 26, 27, 28, 29, 30, 31,
    // 32, 33, 34, 35, 36, 37, 38, 39, 32, 33, 34, 35, 36, 37, 38, 39, 32, 33,
    // 34, 35, 36, 37, 38, 39, 32, 33, 34, 35, 36, 37, 38, 39,
    // 40, 41, 42, 43, 44, 45, 46, 47, 40, 41, 42, 43, 44, 45, 46, 47, 40, 41,
    // 42, 43, 44, 45, 46, 47, 40, 41, 42, 43, 44, 45, 46, 47,
    // 48, 49, 50, 51, 52, 53, 54, 55, 48, 49, 50, 51, 52, 53, 54, 55, 48, 49,
    // 50, 51, 52, 53, 54, 55, 48, 49, 50, 51, 52, 53, 54, 55,
    // 56, 57, 58, 59, 60, 61, 62, 63, 56, 57, 58, 59, 60, 61, 62, 63, 56, 57,
    // 58, 59, 60, 61, 62, 63, 56, 57, 58, 59, 60, 61, 62, 63

    // ==============Cb============== ==============Cr==============

    // 0, 1, 2, 3, | 4, 5, 6, 7, 0, 1, 2, 3, | 4, 5, 6, 7,
    // 8, 9, 10, 11, | 12, 13, 14, 15, 8, 9, 10, 11, | 12, 13, 14, 15,
    // 16, 17, 18, 19, | 20, 21, 22, 23, 16, 17, 18, 19, | 20, 21, 22, 23,
    // 24, 25, 26, 27, | 28, 29, 30, 31, 24, 25, 26, 27, | 28, 29, 30, 31,
    // ----------------| --------------- --------------- | ---------------
    // 32, 33, 34, 35, | 36, 37, 38, 39, 32, 33, 34, 35, | 36, 37, 38, 39,
    // 40, 41, 42, 43, | 44, 45, 46, 47, 40, 41, 42, 43, | 44, 45, 46, 47,
    // 48, 49, 50, 51, | 52, 53, 54, 55, 48, 49, 50, 51, | 52, 53, 54, 55,
    // 56, 57, 58, 59, | 60, 61, 62, 63, 56, 57, 58, 59, | 60, 61, 62, 63,

    // Pixel Matrix

    // 0, 1, 2, 3, 4, 5, 6, 7, | 8, 9, 10, 11, 12, 13, 14, 15,
    // 16, 17, 18, 19, 20, 21, 22, 23, | 24, 25, 26, 27, 28, 29, 30, 31,
    // 32, 33, 34, 35, 36, 37, 38, 39, | 40, 41, 42, 43, 44, 45, 46, 47,
    // 48, 49, 50, 51, 52, 53, 54, 55, | 56, 57, 58, 59, 60, 61, 62, 63,
    // 64, 65, 66, 67, 68, 69, 70, 71, | 72, 73, 74, 75, 76, 77, 78, 79,
    // 80, 81, 82, 83, 84, 85, 86, 87, | 88, 89, 90, 91, 92, 93, 94, 95,
    // 96, 97, 98, 99, 100, 101, 102, 103, | 104, 105, 106, 107, 108, 109, 110,
    // 111,
    // 112, 113, 114, 115, 116, 117, 118, 119, | 120, 121, 122, 123, 124, 125,
    // 126, 127,
    // ----------------------------------------|---------------------------------------
    // 128, 129, 130, 131, 132, 133, 134, 135, | 136, 137, 138, 139, 140, 141,
    // 142, 143,
    // 144, 145, 146, 147, 148, 149, 150, 151, | 152, 153, 154, 155, 156, 157,
    // 158, 159,
    // 160, 161, 162, 163, 164, 165, 166, 167, | 168, 169, 170, 171, 172, 173,
    // 174, 175,
    // 176, 177, 178, 179, 180, 181, 182, 183, | 184, 185, 186, 187, 188, 189,
    // 190, 191,
    // 192, 193, 194, 195, 196, 197, 198, 199, | 200, 201, 202, 203, 204, 205,
    // 206, 207,
    // 208, 209, 210, 211, 212, 213, 214, 215, | 216, 217, 218, 219, 220, 221,
    // 222, 223,
    // 224, 225, 226, 227, 228, 229, 230, 231, | 232, 233, 234, 235, 236, 237,
    // 238, 239,
    // 240, 241, 242, 243, 244, 245, 246, 247, | 248, 249, 250, 251, 252, 253,
    // 254, 255,

    // The four Luma 8x8 matrices (quadrants Y0, Y1, Y2, Y3) form the basis of
    // the final 16x16 pixel matrix.
    // The two Croma 8x8 matrices are used to calculate the actual RGB value of
    // the pixel (RGB565, each pixel is represented by two bytes)

    // Each processing loop processes from each Luma matrix two rows. In each
    // 'two row' loop the rows are processed
    // by two columns.

    // First Loop will take (assume there is only one pixel matrix to fill):

    // Quadrant 1
    // From Cb -> 0
    // From Cr -> 0
    // From Y0 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 0, 16 and 1 and 17

    // Quadrant 2
    // From Cb -> 4
    // From Cr -> 4
    // From Y1 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 8, 24 and 9 and 25

    // Quadrant 3
    // From Cb -> 32
    // From Cr -> 32
    // From Y2 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 128, 144 and 129 and 145

    // Quadrant 4
    // From Cb -> 36
    // From Cr -> 36
    // From Y3 -> 0, 8 and 1, 9 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 136, 152 and 137 and 153

    // Second Loop will take (assume there is only one pixel matrix to fill):

    // Quadrant 1
    // From Cb -> 1
    // From Cr -> 1
    // From Y0 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 2, 18 and 3 and 19

    // Quadrant 2
    // From Cb -> 5
    // From Cr -> 5
    // From Y1 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 10, 26 and 11 and 27

    // Quadrant 3
    // From Cb -> 33
    // From Cr -> 33
    // From Y2 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 130, 146 and 131 and 147
    // Quadrant 4
    // From Cb -> 37
    // From Cr -> 37
    // From Y3 -> 2, 10 and 3, 11 - use Cb and Cr to calculate RGB and place in
    // pixel matrix in 138, 154 and 139 and 155

    // We need third and fourth loop to complete first two lines of the luma
    // blocks. At this time we
    // have written 64 pixels to the pixel matrix.

    // These four loops have to be repeated 4 more times (4 * 64 = 256) to fill
    // complete pixel matrix.

    // Remark the offsets to use in the pixel matrix have to take into account
    // that an GroupOfBlocks contains multiple pixel matrices.
    // So to calculate the real index we have to take that also into account
    // (blockCount)

    // contains common code for optimization purposes
    /*
    private int peekStreamData(ByteBuffer stream, int count)
    {
        int data = 0;
        int stream_field = streamField;
        int stream_field_bit_index = streamFieldBitIndex;
        while (count > (32 - stream_field_bit_index) && streamIndex < (imageStreamCapacity >> 2))
        {
            data = (data << (32 - stream_field_bit_index)) | (stream_field >>> stream_field_bit_index);
            count -= 32 - stream_field_bit_index;
            stream_field = ((imageStreamByteArray[streamIndex * 4 + 0] & 0xFF) | ((imageStreamByteArray[streamIndex * 4 + 1] & 0xFF) << 8) | ((imageStreamByteArray[streamIndex * 4 + 2] & 0xFF) << 16) | ((imageStreamByteArray[streamIndex * 4 + 3] & 0xFF) << 24));
            stream_field_bit_index = 0;
        }
        if (count > 0)
            data = (data << count) | (stream_field >>> (32 - count));
        return data;
    }
     */
    private void processStream()
    {
        boolean blockY0HasAcComponents = false;
        boolean blockY1HasAcComponents = false;
        boolean blockY2HasAcComponents = false;
        boolean blockY3HasAcComponents = false;
        boolean blockCbHasAcComponents = false;
        boolean blockCrHasAcComponents = false;

        // Set streamFieldBitIndex to 32 to make sure that the first call to
        // ReadStreamData
        // actually consumes data from the stream
        streamFieldBitIndex = 32;
        streamField = 0;
        streamIndex = 0;
        sliceIndex = 0;
        pictureComplete = false;

        while (!pictureComplete && streamIndex < (imageStreamCapacity >> 2))
        {
            readHeader();

            if (!pictureComplete)
            {
                for (int count = 0; count < blockCount; count++)
                {
                    int macroBlockEmpty = readStreamDataInt(1);
                    if (macroBlockEmpty == 0)
                    {
                        int acCoefficientsTemp = readStreamDataInt(8);
                        blockY0HasAcComponents = (acCoefficientsTemp >>> 0 & 1) == 1;
                        blockY1HasAcComponents = (acCoefficientsTemp >>> 1 & 1) == 1;
                        blockY2HasAcComponents = (acCoefficientsTemp >>> 2 & 1) == 1;
                        blockY3HasAcComponents = (acCoefficientsTemp >>> 3 & 1) == 1;
                        blockCbHasAcComponents = (acCoefficientsTemp >>> 4 & 1) == 1;
                        blockCrHasAcComponents = (acCoefficientsTemp >>> 5 & 1) == 1;

                        if ((acCoefficientsTemp >>> 6 & 1) == 1)
                        {
                            int quantizer_modeTemp = readStreamDataInt(2);
                            quantizerMode = (int) ((quantizer_modeTemp < 2) ? ~quantizer_modeTemp : quantizer_modeTemp);
                        }

                        getBlockBytes(blockY0HasAcComponents);
                        inverseTransform(count, 0);

                        getBlockBytes(blockY1HasAcComponents);
                        inverseTransform(count, 1);

                        getBlockBytes(blockY2HasAcComponents);
                        inverseTransform(count, 2);

                        getBlockBytes(blockY3HasAcComponents);
                        inverseTransform(count, 3);

                        getBlockBytes(blockCbHasAcComponents);
                        inverseTransform(count, 4);

                        getBlockBytes(blockCrHasAcComponents);
                        inverseTransform(count, 5);
                    }
                }
                composeImageSlice();
            }
        }
    }

    private void readHeader()
    {
        alignStreamData();

        int code = readStreamDataInt(22);
        int startCode = code & (~0x1F);

        if (startCode == 32)
        {
            if ((code & 0x1F) == 0x1F)
            {
                pictureComplete = true;
            } else
            {
                if (sliceIndex++ == 0)
                {
                    pictureFormat = readStreamDataInt(2);
                    resolution = readStreamDataInt(3);
                    pictureType = readStreamDataInt(3);
                    quantizerMode = readStreamDataInt(5);
                    frameIndex = readStreamDataInt(32);

                    switch (pictureFormat)
                    {
                    case CIF:
                        width = CIF_WIDTH << resolution - 1;
                        height = CIG_HEIGHT << resolution - 1;
                        break;
                    case QVGA:
                        width = VGA_WIDTH << resolution - 1;
                        height = VGA_HEIGHT << resolution - 1;
                        break;
                    }

                    // We assume two bytes per pixel (RGB 565)
                    pixelRowSize = width << 1;

                    sliceCount = height >> 4;
                    blockCount = width >> 4;

                    if (imageSlice == null || imageSlice.MacroBlocks.length != blockCount)
                    {
                        imageSlice = new ImageSlice(blockCount);
                        javaPixelData = new int[width * height];
                    }
                } else
                {
                    quantizerMode = readStreamDataInt(5);
                }
            }
        }
    }

    private int readStreamDataInt(int count)
    {
        int data = 0;
        while (count > (32 - streamFieldBitIndex))
        {
            data = data << (32 - streamFieldBitIndex) | (streamField >>> streamFieldBitIndex);
            count -= 32 - streamFieldBitIndex;
            streamField = ((imageStreamByteArray[streamIndex * 4 + 0] & 0xFF) | ((imageStreamByteArray[streamIndex * 4 + 1] & 0xFF) << 8) | ((imageStreamByteArray[streamIndex * 4 + 2] & 0xFF) << 16) | ((imageStreamByteArray[streamIndex * 4 + 3] & 0xFF) << 24));
            streamFieldBitIndex = 0;
            streamIndex++;
        }
        if (count > 0)
        {
            data = (data << count) | (streamField >>> (32 - count));
            streamField <<= count;
            streamFieldBitIndex += count;
        }
        return data;
    }
}
