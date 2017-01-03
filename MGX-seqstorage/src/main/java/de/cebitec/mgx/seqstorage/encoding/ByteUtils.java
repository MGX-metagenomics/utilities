package de.cebitec.mgx.seqstorage.encoding;

import java.util.Arrays;

/**
 *
 * @author sjaenick
 */
public class ByteUtils {

    public static byte[] concat(byte[] b1, byte[] b2) {
        byte[] tmp = Arrays.copyOf(b1, b1.length + b2.length);
        System.arraycopy(b2, 0, tmp, b1.length, b2.length);
        return tmp;
    }

    public static byte[] substring(byte[] b, int from, int to) {
        byte[] ret = new byte[to - from + 1];
        System.arraycopy(b, from, ret, 0, to - from + 1);
        return ret;
    }

//    public static int indexOf(byte[] in, byte separator) {
//        for (int i = 0; i <= in.length - 1; i++) {
//            if (in[i] == separator) {
//                return i;
//            }
//        }
//        return -1;
//    }
    public static int indexOf(final byte[] in, final byte separator) {
        for (int i = 0; i < in.length; i++) {
            if (in[i] == separator) {
                return i;
            }
        }
        return -1;
    }

    public static long bytesToLong(final byte[] b, final int startIdx) {
        long offset = 0;
        offset = (offset << 8) + (b[startIdx] & 0xff);
        offset = (offset << 8) + (b[startIdx + 1] & 0xff);
        offset = (offset << 8) + (b[startIdx + 2] & 0xff);
        offset = (offset << 8) + (b[startIdx + 3] & 0xff);
        offset = (offset << 8) + (b[startIdx + 4] & 0xff);
        offset = (offset << 8) + (b[startIdx + 5] & 0xff);
        offset = (offset << 8) + (b[startIdx + 6] & 0xff);
        offset = (offset << 8) + (b[startIdx + 7] & 0xff);
        return offset;
    }

    public static long bytesToLong(final byte[] b) {
        long offset = 0;
        offset = (offset << 8) + (b[0] & 0xff);
        offset = (offset << 8) + (b[1] & 0xff);
        offset = (offset << 8) + (b[2] & 0xff);
        offset = (offset << 8) + (b[3] & 0xff);
        offset = (offset << 8) + (b[4] & 0xff);
        offset = (offset << 8) + (b[5] & 0xff);
        offset = (offset << 8) + (b[6] & 0xff);
        offset = (offset << 8) + (b[7] & 0xff);
        return offset;
    }

    public static byte[] longToBytes(final long l) {
        byte[] ret = new byte[8];
        ret[0] = (byte) (0xff & (l >> 56));
        ret[1] = (byte) (0xff & (l >> 48));
        ret[2] = (byte) (0xff & (l >> 40));
        ret[3] = (byte) (0xff & (l >> 32));
        ret[4] = (byte) (0xff & (l >> 24));
        ret[5] = (byte) (0xff & (l >> 16));
        ret[6] = (byte) (0xff & (l >> 8));
        ret[7] = (byte) (0xff & (l >> 0));
        return ret;
    }

    public static byte[] longsToBytes(final long l1, final long l2) {
        byte[] ret = new byte[16];
        longsToBytes(l1, l2, ret);
        return ret;
    }

    public static void longsToBytes(long l1, long l2, byte[] dest) {
        dest[0] = (byte) (0xff & (l1 >> 56));
        dest[1] = (byte) (0xff & (l1 >> 48));
        dest[2] = (byte) (0xff & (l1 >> 40));
        dest[3] = (byte) (0xff & (l1 >> 32));
        dest[4] = (byte) (0xff & (l1 >> 24));
        dest[5] = (byte) (0xff & (l1 >> 16));
        dest[6] = (byte) (0xff & (l1 >> 8));
        dest[7] = (byte) (0xff & (l1 >> 0));
        //
        dest[8] = (byte) (0xff & (l2 >> 56));
        dest[9] = (byte) (0xff & (l2 >> 48));
        dest[10] = (byte) (0xff & (l2 >> 40));
        dest[11] = (byte) (0xff & (l2 >> 32));
        dest[12] = (byte) (0xff & (l2 >> 24));
        dest[13] = (byte) (0xff & (l2 >> 16));
        dest[14] = (byte) (0xff & (l2 >> 8));
        dest[15] = (byte) (0xff & (l2 >> 0));
    }
}
