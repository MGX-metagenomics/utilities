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
public class SFFHeader {

    public static SFFHeader readFrom(RandomAccessFile raf) throws IOException {
        raf.seek(0);
        long magic = Util.readUint32(raf);
        assert magic == 0x2E736666;
        byte[] version = new byte[4];
        raf.read(version);

        long index_offset = Util.readUint64(raf);
        long index_length = Util.readUint32(raf);
        long numReads = Util.readUint32(raf);
        int headerLength = Util.readUint16(raf);
        int keyLength = Util.readUint16(raf);
        int number_of_flows_per_read = Util.readUint16(raf);
        int flowgram_format_code = Util.readUint8(raf);
        if (flowgram_format_code != 1) {
            System.err.println("Unknown flowgram format code: "+flowgram_format_code);
            assert false;
        }

        byte[] flow_chars = new byte[number_of_flows_per_read];
        raf.read(flow_chars);

        byte[] key_sequence = new byte[keyLength];
        raf.read(key_sequence);

        Util.pad8(raf);

        return new SFFHeader(index_offset, index_length, numReads, headerLength, keyLength, number_of_flows_per_read, flowgram_format_code, flow_chars, key_sequence);
    }

    private final long index_offset;
    private final long index_length;
    private final long numReads;
    private final int headerLength;
    private final int keyLength;
    private final int number_of_flows_per_read;
    private final int flowgram_format_code;
    private final byte[] flow_chars;
    private final byte[] key_sequence;
    private final int flowgram_bytes_per_flow;

    public SFFHeader(long index_offset, long index_length, long numReads, int headerLength, int keyLength, int number_of_flows_per_read, int flowgram_format_code, byte[] flow_chars, byte[] key_sequence) {
        this.index_offset = index_offset;
        this.index_length = index_length;
        this.numReads = numReads;
        this.headerLength = headerLength;
        this.keyLength = keyLength;
        this.number_of_flows_per_read = number_of_flows_per_read;
        this.flowgram_format_code = flowgram_format_code;
        this.flow_chars = flow_chars;
        this.key_sequence = key_sequence;
        flowgram_bytes_per_flow = 2;
    }

    public long getIndexOffset() {
        return index_offset;
    }

    public long getIndexLength() {
        return index_length;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public int getNumberOfFlows() {
        return number_of_flows_per_read;
    }

    public int getFlowgramBytesPerFlow() {
        return flowgram_bytes_per_flow;
    }

    public int getFlowgramFormatCode() {
        return flowgram_format_code;
    }

    public long getNumberOfReads() {
        return numReads;
    }

    public String getFlowChars() {
        return new String(flow_chars);
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getKeySequence() {
        return new String(key_sequence);
    }

}
