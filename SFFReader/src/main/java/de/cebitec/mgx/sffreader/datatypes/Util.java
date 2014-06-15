/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author sj
 */
public class Util {

    //
    // all values assumed to be big endian
    //
    public static int readUint8(RandomAccessFile is) throws IOException {
        return is.read() & 0xff;
    }

    public static int readUint16(RandomAccessFile is) throws IOException {
        int i1 = is.read() & 0xff;
        int i2 = is.read() & 0xff;
        return (i2 | i1 << 8) & 0xffff;
    }

    public static long readUint32(RandomAccessFile is) throws IOException {
        long i;
        int b1, b2, b3, b4;
        b1 = is.read() & 0xff;
        b2 = is.read() & 0xff;
        b3 = is.read() & 0xff;
        b4 = is.read() & 0xff;
        i = (b4 | b3 << 8 | b2 << 16 | b1 << 24) & 0xffffffffL;
        return i;
    }

    public static long readUint64(RandomAccessFile is) throws IOException {
        long i;
        long b1, b2, b3, b4, b5, b6, b7, b8;

        b1 = is.read() & 0xff;
        b2 = is.read() & 0xff;
        b3 = is.read() & 0xff;
        b4 = is.read() & 0xff;
        b5 = is.read() & 0xff;
        b6 = is.read() & 0xff;
        b7 = is.read() & 0xff;
        b8 = is.read() & 0xff;

        i = b8 | b7 << 8 | b6 << 16 | b5 << 24 | b4 << 32 | b3 << 40 | b2 << 48 | b1 << 56;
        return i;
    }

    public static long eightBytePadding(long offset) {
        int align = 8;
        offset = offset + ((align - (offset % align)) % align);
        return offset;
    }

    public static void pad8(RandomAccessFile raf) throws IOException {
        raf.seek(eightBytePadding(raf.getFilePointer()));
    }

    public static void readUint8s(RandomAccessFile is, int[] ret) throws IOException {
        for (int i=0; i<ret.length;i++) {
            ret[i] = readUint8(is);
        }
    }

    public static void readChars(RandomAccessFile raf, char[] ret) throws IOException {
        byte[] buf = new byte[ret.length * 2];
        raf.read(buf);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = convert(buf, 2 * i);
        }
    }

    private static char convert(byte[] in, int pos) {
        return (char) ((in[pos] << 8) | in[pos + 1]);
    }

}
