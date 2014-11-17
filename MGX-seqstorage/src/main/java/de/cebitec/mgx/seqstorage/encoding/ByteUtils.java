package de.cebitec.mgx.seqstorage.encoding;

/**
 *
 * @author sjaenick
 */
public class ByteUtils {

    public static byte[] concat(byte[] b1, byte[] b2) {
        byte[] tmp = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, tmp, 0, b1.length);
        System.arraycopy(b2, 0, tmp, b1.length, b2.length);
        return tmp;
    }

    public static byte[] substring(byte[] b, int from, int to) {
        byte[] ret = new byte[to - from + 1];
        System.arraycopy(b, from, ret, 0, to - from + 1);
        return ret;
    }

    public static long bytesToLong(byte[] b) {
        long offset = 0;
        for (int i = 0; i < 8; i++) {
            offset = (offset << 8) + (b[i] & 0xff);
        }
        return offset;
    }

    public static byte[] longToBytes(long l) {
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
}
