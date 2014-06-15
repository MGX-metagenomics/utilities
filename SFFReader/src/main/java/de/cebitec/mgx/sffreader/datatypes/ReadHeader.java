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
public class ReadHeader {

    public static ReadHeader readFrom(RandomAccessFile raf) throws IOException {

        int read_header_length = Util.readUint16(raf);
        int name_length = Util.readUint16(raf);
        long number_of_bases = Util.readUint32(raf);
        int clip_qual_left = Util.readUint16(raf);
        int clip_qual_right = Util.readUint16(raf);
        int clip_adapter_left = Util.readUint16(raf);
        int clip_adapter_right = Util.readUint16(raf);
        byte[] name = new byte[name_length];
        raf.read(name);
        //Util.readChars(raf, name);
        Util.pad8(raf);
        return new ReadHeader(read_header_length, name_length, number_of_bases, clip_qual_left, clip_qual_right, clip_adapter_left, clip_adapter_right, name);
    }

    private final int read_header_length;
    private final int name_length;
    private final long number_of_bases;
    private final int clip_qual_left;
    private final int clip_qual_right;
    private final int clip_adapter_left;
    private final int clip_adapter_right;
    private final byte[] name;

    public ReadHeader(int read_header_length, int name_length, long number_of_bases, int clip_qual_left, int clip_qual_right, int clip_adapter_left, int clip_adapter_right, byte[] name) {
        this.read_header_length = read_header_length;
        this.name_length = name_length;
        this.number_of_bases = number_of_bases;
        this.clip_qual_left = clip_qual_left;
        this.clip_qual_right = clip_qual_right;
        this.clip_adapter_left = clip_adapter_left;
        this.clip_adapter_right = clip_adapter_right;
        this.name = name;
    }

    public int getNumberOfBases() {
        return (int) number_of_bases;
    }

    public int getClipQLeft() {
        return clip_qual_left;
    }

    public int getClipQRight() {
        return clip_qual_right;
    }

    public int getClipAdapterLeft() {
        return clip_adapter_left;
    }

    public int getClipAdapterRight() {
        return clip_adapter_right;
    }

    public String getName() {
        return new String(name);
    }

    public int getClipLeft() {
        return Math.max(1, Math.max(clip_qual_left, clip_adapter_left));
    }

    public int getClipRight() {
        return Math.min((clip_qual_right == 0 ? getNumberOfBases()-1 : clip_qual_right), (clip_adapter_right == 0 ? getNumberOfBases()-1 : clip_adapter_right));
    }
}
