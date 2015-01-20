/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.DNAQualitySequenceHolder;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sffreader.datatypes.SFFRead;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class SFFReader implements SeqReaderI<DNAQualitySequenceHolder> {

    private final de.cebitec.mgx.sffreader.SFFReader reader;
    private final String file;
    private DNAQualitySequenceHolder holder = null;

    public SFFReader(String uri) throws SeqStoreException{
        this.file = uri;        
        try {
            reader = new de.cebitec.mgx.sffreader.SFFReader(uri);
        } catch (IOException ex) {
            throw new SeqStoreException("File not found or unreadable: " + file + "\n" + ex.getMessage());
        }                
    }

    @Override
    public void delete() {
        new File(file).delete();
    }

    @Override
    public Set<DNAQualitySequenceHolder> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceHolder> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i =0; i< ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNAQualitySequenceHolder elem = nextElement();
            if (idList.contains(elem.getSequence().getId())) {
                res.add(elem);
                idList.remove(elem.getSequence().getId());
            }
        }

        if (!idList.isEmpty()) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return res;
    }
    
    
    @Override
    public boolean hasMoreElements() {
        if (reader.hasMoreElements()){
            SFFRead read = reader.nextElement();

            QualityDNASequence seq;
            
            seq = new QualityDNASequence();
            seq.setName(read.getName().getBytes());
            seq.setSequence(read.getBases().getBytes());
            seq.setQuality(read.getQuality());
            
            holder = new DNAQualitySequenceHolder(seq);
            return true;
        } else {
            return false;
        }        
    }

    @Override
    public DNAQualitySequenceHolder nextElement() {
        DNAQualitySequenceHolder ret = holder;
        holder = null;
        return ret;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public boolean hasQuality() {
        return true;
    }
}
