package de.cebitec.mgx.sffreader;

import de.cebitec.mgx.braf.BufferedRandomAccessFile;
import de.cebitec.mgx.sffreader.datatypes.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 *
 * @author sj
 */
public class SFFReader implements Enumeration<SFFRead>{

    private final SFFHeader header;
    private final SFFIndex idx;
    private final RandomAccessFile raf;
    private long nextEntry;
    private long firstEntry;
    
    public SFFReader(String file) throws IOException {
        this(new BufferedRandomAccessFile(file, "r"));
    }

    public SFFReader(RandomAccessFile raf) throws IOException {
        this.raf = raf;
        header = SFFHeader.readFrom(raf);
        firstEntry = raf.getFilePointer();
        nextEntry = 0;
        
        if (header.getIndexOffset() != 0 && header.getIndexLength() != 0) {
            idx = SFFIndex.readFrom(raf, header);
        } else {
            idx = null;
        }
        raf.seek(firstEntry);
    }

    public long getIndexOffset() {
        return header.getIndexOffset();
    }

    public long getNumberOfReads() {
        return header.getNumberOfReads();
    }

    public String getKeySequence() {
        return header.getKeySequence();
    }

    public String getFlowChars() {
        return header.getFlowChars();
    }

//    public int size() {
//        return idx.size();
//    }
//
//    public Set<String> keySet() {
//        return idx.keySet();
//    }
//
//    public long getOffset(String s) {
//        return idx.getOffSet(s);
//    }
//
//    public String getRead(String name) throws IOException {
//        long offset = idx.getOffSet(name);
//        raf.seek(offset);
//        ReadHeader rh = ReadHeader.readFrom(raf);
//        ReadData rd = ReadData.readFrom(raf, header, rh);
//        String dna = rd.getBases();
//        return dna.substring(rh.getClipLeft() - 1, rh.getClipRight() );
//    }

    public void close() throws IOException {
        raf.close();
    }

//    public byte[] getQuality(String name) throws IOException {
//        long offset = idx.getOffSet(name);
//        raf.seek(offset);
//        ReadHeader rh = ReadHeader.readFrom(raf);
//        ReadData rd = ReadData.readFrom(raf, header, rh);
//        int[] qScores = rd.getQualityScores();
//        int[] trimmed = Arrays.copyOfRange(qScores, rh.getClipLeft() - 1, rh.getClipRight() );
//        byte[] ret = new byte[trimmed.length];
//        for (int i =0; i< ret.length; i++) {
//            ret[i] = (byte)trimmed[i];
//        }
//        return ret;
//    }

    @Override
    public boolean hasMoreElements(){
        return (nextEntry < header.getNumberOfReads());
    }
    
    @Override
    public SFFRead nextElement(){
        try {
            ReadHeader rh = ReadHeader.readFrom(raf);
            ReadData rd = ReadData.readFrom(raf, header, rh);
            int[] qScores = rd.getQualityScores();
            int[] trimmed = Arrays.copyOfRange(qScores, rh.getClipLeft() - 1, rh.getClipRight() );
            byte[] quality = new byte[trimmed.length];
            for (int i =0; i< quality.length; i++) {
                quality[i] = (byte)trimmed[i];
            }
            nextEntry++;
            return new SFFRead( rh.getName(),
                    rd.getBases().substring(rh.getClipLeft() - 1, rh.getClipRight()),
                    quality);
        } catch (IOException ex) {
            throw new NoSuchElementException(ex.getMessage());
        }
    }
    
    public void reset() throws IOException{
        raf.seek(firstEntry);
    }

    public SFFRead getRead(String name) throws IOException {
        long rafOffset = raf.getFilePointer();
        
        if (idx == null){    
            raf.seek(firstEntry);
            String entryName;
            ReadHeader rh;
            ReadData rd;
            long currentEntry = 0;
            do{
                rh = ReadHeader.readFrom(raf);
                rd = ReadData.readFrom(raf, header, rh);
                entryName = rh.getName();
                currentEntry++;
            } while (!entryName.equals(name) && currentEntry<header.getNumberOfReads());

            raf.seek(rafOffset);

            if (entryName.equals(name)){
                int[] qScores = rd.getQualityScores();
                int[] trimmed = Arrays.copyOfRange(qScores, rh.getClipLeft() - 1, rh.getClipRight() );
                byte[] quality = new byte[trimmed.length];
                for (int i =0; i< quality.length; i++) {
                    quality[i] = (byte)trimmed[i];
                }
                return new SFFRead( entryName, 
                                rd.getBases().substring(rh.getClipLeft() - 1, rh.getClipRight()),
                                quality);
            } else {
                return null;
            }
        } else {
            long offset = idx.getOffSet(name);
            if (offset == -1) return null;
            raf.seek(offset);
            ReadHeader rh = ReadHeader.readFrom(raf);
            ReadData rd = ReadData.readFrom(raf, header, rh);
            int[] qScores = rd.getQualityScores();
            int[] trimmed = Arrays.copyOfRange(qScores, rh.getClipLeft() - 1, rh.getClipRight() );
            byte[] quality = new byte[trimmed.length];
            for (int i =0; i< quality.length; i++) {
                quality[i] = (byte)trimmed[i];
            }
            raf.seek(rafOffset);
            return new SFFRead( name, 
                                rd.getBases().substring(rh.getClipLeft() - 1, rh.getClipRight()),
                                quality);
        }        
    }
    
}
