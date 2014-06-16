/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

/**
 *
 * @author sj
 */
public class SFFIndex {

    private final TObjectLongMap<String> reads;

    public static SFFIndex readFrom(RandomAccessFile raf, SFFHeader hdr) throws IOException {
        raf.seek(hdr.getIndexOffset());
        long idxMagic = Util.readUint32(raf);
        if (idxMagic != 778921588) {
            System.err.print(raf.getFilePointer() -4);
            assert false;
        }

        byte[] index_version = new byte[4];
        raf.read(index_version);
        TObjectLongMap<String> reads = new TObjectLongHashMap<>(10, 0.5f, -1);
        long offset = Util.eightBytePadding(hdr.getHeaderLength());
        while (offset < hdr.getIndexOffset()) {
            raf.seek(offset);
            byte[] readHeaderLength = new byte[2];
            raf.read(readHeaderLength);
            int nameLength = Util.readUint16(raf);
            long numBases = Util.readUint32(raf);
            raf.seek(raf.getFilePointer() + 8);
            byte[] name = new byte[nameLength];
            raf.read(name);
            reads.put(new String(name), offset);
            long dataLen = hdr.getNumberOfFlows() * hdr.getFlowgramBytesPerFlow() + 3 * numBases;
            offset = Util.eightBytePadding(Util.eightBytePadding(raf.getFilePointer()) + dataLen);
        }

        Util.pad8(raf);

        raf.seek(hdr.getIndexOffset() + hdr.getIndexLength());
        return new SFFIndex(reads);
    }

    public SFFIndex(TObjectLongMap<String> reads) {
        this.reads = reads;
    }

    public int size() {
        return reads.size();
    }

    public Set<String> keySet() {
        return reads.keySet();
    }

    public long getOffSet(String key) {
        return reads.get(key);
    }

}
