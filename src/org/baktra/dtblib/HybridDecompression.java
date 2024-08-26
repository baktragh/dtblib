package org.baktra.dtblib;
import java.util.logging.Level;


public class HybridDecompression {

    public static class ResultCrate {

        public ResultCrate(int newPosition, Segment newSegment) {
            this.newPosition = newPosition;
            this.newSegment = newSegment;
        }

        final int newPosition;
        final Segment newSegment;

    }
    
    protected static final int COMPRESS_LZ4=0;
    protected static final int COMPRESS_APLIB=1;
    protected static final int COMPRESS_ZX0=2;
    

    protected ResultCrate processLZ4(int[] fileData, int pos, int firstAddress, int rba, String filename) throws DOS2BinaryException {

        int originalPos = pos;

        /*Now decompress the data. The decompressed data is not that important, but we need to find end of the segment*/
        try {

            while (true) {

                int token;
                int litLen;

                /*Get token*/
                token = fileData[pos];
                pos++;

                /*If there is a literal*/
                if ((token >> 4) != 0) {

                    /*Get initial length*/
                    litLen = (token >> 4);

                    /*If more bytes of length*/
                    if (litLen == 0x0F) {

                        int b;

                        do {
                            /*Get another byte*/
                            b = fileData[pos];
                            pos++;
                            /*Add to the literal length*/
                            litLen += b;
                        } while (b == 0xFF);

                    }

                    /*Skip the literal of given length*/
                    pos += litLen;
                }

                /*Get another two bytes for matchcopy*/
                int b1 = fileData[pos];
                pos++;
                int b2 = fileData[pos];
                pos++;

                /*If both zero, then decompression is complete*/
                if (b1 == 0 && b2 == 0) {
                    break;
                }

                /*Otherwise, we have backward links*/
                int matchLen = 0x04 + (token & 0x0F);

                if (matchLen == 0x13) {
                    do {
                        b1 = fileData[pos];
                        pos++;
                        matchLen += b1;
                    } while (b1 == 0xFF);
                }

            }

            /*Now we can construct a segment and add it to the binary load file*/
            int[] rawData = new int[pos - originalPos];
            System.arraycopy(fileData, originalPos, rawData, 0, rawData.length);
            
            
            Segment s = new Segment(firstAddress, rawData, rba, new int[0],COMPRESS_LZ4);
            return new ResultCrate(pos, s);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DOS2BinaryException(filename, "Compressed data continue beyond end of file", pos);
        } catch (Exception e2) {
            throw new DOS2BinaryException(filename, "Decompression failed", pos);

        }

    }

    protected ResultCrate processZX0(int[] fileData, int pos, int firstAddress, int rba, String filename) throws DOS2BinaryException {

        try {
            int originalPos = pos;

            int state = ZX0_LITERAL;
            MiniBitStream bs = new MiniBitStream(fileData, pos);

            outerLoop:
            while (true) {

                switch (state) {
                    case ZX0_LITERAL: {

                        /*Get elias gamma*/
                        int literalSize = getEliasGamma(bs);

                        /*Update posotion*/
                        for (int i = 0; i < literalSize; i++) {
                            bs.getNextByte();
                        }


                        /*Determine what next*/
                        boolean b = bs.getNextBit();

                        if (b == true) {
                            state = ZX0_COPY_FROM_NEW;
                        } else {
                            state = ZX0_COPY_FROM_LAST;
                        }
                        break;
                    }
                    case ZX0_COPY_FROM_LAST: {

                        getEliasGamma(bs);

                        if (!bs.getNextBit()) {
                            state = ZX0_LITERAL;
                        } else {
                            state = ZX0_COPY_FROM_NEW;
                        }
                        break;
                    }
                    case ZX0_COPY_FROM_NEW: {

                        int msb = getEliasGamma(bs);

                        /*if msb equal to 256, we reached EOF*/
                        if (msb == 256) {
                            state = ZX0_EOF;
                            break;
                        }

                        /*LSB - 7 bits*/
                        bs.getNextByte();
                        bs.backTrack();

                        /*Number of repetitions - 1*/
                        getEliasGamma(bs);

                        /*Continuation*/
                        if (bs.getNextBit()) {
                            state = ZX0_COPY_FROM_NEW;
                        } else {
                            state = ZX0_LITERAL;
                        }
                        

                        break;
                    }
                    case ZX0_EOF: {
                        break outerLoop;
                    }

                }

            }

            pos = bs.getPosition();

            /*Now we can construct a segment and add it to the binary load file*/
            int[] rawData = new int[pos - originalPos];
            System.arraycopy(fileData, originalPos, rawData, 0, rawData.length);

            Segment s = new Segment(firstAddress, rawData, rba, new int[0],COMPRESS_ZX0);
            return new ResultCrate(pos, s);

        } catch (Exception e2) {
            throw new DOS2BinaryException(filename, "Decompression failed", pos);

        }
    }

    private int getEliasGamma(MiniBitStream bs) {

        int value = 1;
        boolean b;
        boolean c;

        while (!(b = bs.getNextBit())) {
            value = value << 1 | ((c = bs.getNextBit()) ? 1 : 0);
        }

        return value;
    }

    private static class MiniBitStream {

        private final int[] fileData;
        private int bitsMask;
        private int position;
        private int latchedByte;
        private boolean backTrack;
        private int lastByte;

        protected MiniBitStream(int[] fileData, int position) {
            this.fileData = fileData;
            this.position = position;
            this.latchedByte = -1;
            this.bitsMask = 0;
            this.backTrack = false;
            this.lastByte = -1;

        }

        protected int getNextByte() {

            int val = fileData[position];
            position++;
            lastByte = val;
            return val;
        }

        protected boolean getNextBit() {

            if (backTrack) {
                backTrack = false;
                return (lastByte & 1) == 1;
            }
            if (bitsMask == 0) {
                latchedByte = getNextByte();
                bitsMask = 128;
            }

            boolean b = ((latchedByte & bitsMask) == bitsMask);
            bitsMask = bitsMask >> 1;

            return b;
        }

        protected int getPosition() {
            return position;
        }

        protected void backTrack() {
            backTrack = true;
        }

    }

    private final int ZX0_LITERAL = 1;
    private final int ZX0_COPY_FROM_LAST = 2;
    private final int ZX0_COPY_FROM_NEW = 3;
    private final int ZX0_EOF = 4;

    ResultCrate processAPlib(int[] fileData, int pos, int firstAddress, int rba, String filename) throws DOS2BinaryException {

        int originalPos = pos;

        try {

            ApDecomp dc = new ApDecomp(fileData, pos);
            int newPos = dc.depack();
            int[] rawData = new int[newPos - originalPos];
            System.arraycopy(fileData, originalPos, rawData, 0, rawData.length);
            Segment s = new Segment(firstAddress, rawData, rba, new int[0],COMPRESS_APLIB);
            return new ResultCrate(newPos, s);
        } catch (Exception e2) {
            throw new DOS2BinaryException(filename, "Decompression failed", pos);
        }

    }

    class ApDecomp {


        int[] sourceData;
        int sourcePos;
        int tag;
        int bitcount;
        public ApDecomp(int[] sourceData, int sourcePos) {
            this.sourceData = sourceData;
            this.sourcePos = sourcePos;
        }

        private int getBit() {

            int bit;

            /* check if tag is empty */
            if ((bitcount--) == 0) {
                /* load next tag */
                tag = sourceData[sourcePos++];
                bitcount = 7;
            }

            /* shift bit out of tag */
            bit = (tag >> 7) & 0x01;
            tag <<= 1;

            return bit;
        }

        int getGamma() {
            int result = 1;

            /* input gamma2-encoded bits */
            do {
                result = (result << 1) + getBit();
            } while (getBit() != 0);

            return result;
        }

        int depack() {
            int offs, len, r0, lwm;
            int done;
            int i;
            bitcount = 0;

            r0 = -1;
            lwm = 0;
            done = 0;

            /* first byte verbatim */
            sourcePos++;

            /* main decompression loop */
            while (done == 0) {
                if (getBit() == 1) {
                    if (getBit() == 1) {
                        if (getBit() == 1) {
                            offs = 0;

                            for (i = 4; i != 0; i--) {
                                offs = (offs << 1) + getBit();
                            }

                            if (offs != 0) {

                            } else {

                            }

                            lwm = 0;
                        } else {
                            offs = sourceData[sourcePos];
                            sourcePos++;

                            len = 2 + (offs & 0x0001);

                            offs >>= 1;

                            if (offs != 0) {
                                for (; len != 0; len--) {

                                }
                            } else {
                                done = 1;
                            }

                            r0 = offs;
                            lwm = 1;
                        }
                    } else {
                        offs = getGamma();

                        if ((lwm == 0) && (offs == 2)) {
                            offs = r0;

                            len = getGamma();

                            for (; len != 0; len--) {

                            }
                        } else {
                            if (lwm == 0) {
                                offs -= 3;
                            } else {
                                offs -= 2;
                            }

                            offs <<= 8;
                            offs += sourceData[sourcePos];
                            sourcePos++;

                            len = getGamma();

                            if (offs >= 32000) {
                                len++;
                            }
                            if (offs >= 1280) {
                                len++;
                            }
                            if (offs < 128) {
                                len += 2;
                            }

                            for (; len != 0; len--) {

                            }

                            r0 = offs;
                        }

                        lwm = 1;
                    }
                } else {
                    sourcePos++;
                    lwm = 0;
                }
            }

            return sourcePos;
        }

    }
}
