package de.cebitec.mgx.seqcompression;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Patrick Blumenkamp
 */
public class QualityEncoder {

    /**
     * Decodes encoded qualities into quality array.
     *
     * @param encodedQualities Encoded Sanger qualities
     * @param decodedQualityLength Length of decoded qualities (should be equal
     * to sequence length)
     * @return Qualities in Sanger format
     */
    public static byte[] decode(byte[] encodedQualities, int decodedQualityLength) throws SequenceException {
        
        if (encodedQualities == null || encodedQualities.length < 2) {
            throw new SequenceException("Unable to decoded null or invalid data.");
        }
        
        if (decodedQualityLength == 0 || encodedQualities.length == 2) {
            return new byte[0];
        }
        byte encodedSize = encodedQualities[0]; //bit size per quality
        byte minValue = encodedQualities[1];    //offset of each value
        byte[] decoded = new byte[decodedQualityLength];
        int decodedPos = 0;
        RingBuffer bitBuffer = new RingBuffer(15);

        for (int i = 2; i < encodedQualities.length; i++) {
            //write next byte into bit buffer
            byte currentByte = encodedQualities[i];
            for (int j = 0; j < 8; j++) {
                bitBuffer.add((byte) (((currentByte & -128) == -128) ? 1 : 0)); // -128 == 10000000
                currentByte = (byte) (currentByte << 1);
            }

            //if bit buffer contains a whole quality entry, write quality value in array
            while (bitBuffer.getUnreadLength() >= encodedSize) {
                byte k = bitBuffer.next();
                for (int j = 1; j < encodedSize; j++) {
                    k = (byte) (k << 1);
                    k = (byte) (bitBuffer.next() | k);
                }

                //if entry is empty (padding) all entries were read
                if (k == 0) {
                    return decoded;
                }

                decoded[decodedPos++] = (byte) (k + minValue);
            }
        }

        return decoded;
    }

    /**
     * Encodes qualities into compressed quality array. Algorithm calculates min
     * and max value and calculates the range between both values. Each quality
     * is saved in ceil(log_2(max-min+1)) bits. The first two bytes are reserved
     * for bit-size of the qualities and the minimal quality-1.
     *
     * @param quality Qualities in Sanger format
     * @return Encoded qualities
     * @throws SeqStoreException Is thrown if qualities are not in Sanger format
     * (Qualities bigger than 93 or smaller than 0)
     */
    public static byte[] encode(byte[] quality) throws SequenceException {
        if (quality == null || quality.length == 0) {
            return new byte[]{0, 0};
        }
        int max = quality[0];
        int min = quality[0];
        for (int i = 1; i < quality.length; i++) {
            if (max < quality[i]) {
                max = quality[i];
            } else if (min > quality[i]) {
                min = quality[i];
            }
        }

        if (max > 93 || min < 0) //biggest possible phred values in sanger format
        {
            throw new SequenceException("Qualities in no valid Sanger format");
        }

        min--;              //value 0 is used for padding
        max = max - min;
        byte compressedSize = (byte) Math.ceil(FastMath.log(max + 1) / FastMath.log(2)); //needed bits for each quality (compressedSize must be 4 with max equals 8)

        //size of the encoded quality array
        //length * size per quality / bit per byte + 2 index bytes at the beginning
        int encodedArraySize = (int) Math.ceil(quality.length * compressedSize / 8.0 + 2);
        byte[] encoded = new byte[encodedArraySize];
        encoded[0] = compressedSize;
        encoded[1] = (byte) min;
        RingBuffer bitBuffer = new RingBuffer(15);
        int resultPos = 2;
        for (int i = 0; i < quality.length; i++) {
            byte currentByte = (byte) (quality[i] - min);
            currentByte = (byte) (currentByte << 8 - compressedSize);   //first 8 minus compressedSize bits must be zero
            //fill bit buffer with next value
            for (int j = 0; j < compressedSize; j++) {
                bitBuffer.add((byte) (((currentByte & -128) == -128) ? 1 : 0)); // -128 == 10000000
                currentByte = (byte) (currentByte << 1);
            }

            //if a whole byte is in bit buffer, write it in encoded quality array
            if (bitBuffer.getUnreadLength() > 7) {
                byte k = bitBuffer.next();
                for (int j = 0; j < 7; j++) {
                    k = (byte) (k << 1);
                    k = (byte) (bitBuffer.next() | k);
                }
                encoded[resultPos++] = k;
            }
        }
        //flush bit buffer
        if (!bitBuffer.empty()) {
            byte k = bitBuffer.next();
            int paddingCount = 7 - bitBuffer.getUnreadLength();
            while (!bitBuffer.empty()) {
                k = (byte) (k << 1);
                k = (byte) (bitBuffer.next() | k);
            }
            k = (byte) (k << paddingCount);
            encoded[resultPos++] = k;
        }

        return encoded;
    }

}
