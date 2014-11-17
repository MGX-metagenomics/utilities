/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqholder.DNAQualitySequenceHolder;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class SFFReader implements SeqReaderI<DNAQualitySequenceHolder> {

    private final de.cebitec.mgx.sffreader.SFFReader reader;
    private final String file;
    private final Iterator<String> iter;

    public SFFReader(String uri) throws IOException {
        this.file = uri;
        reader = new de.cebitec.mgx.sffreader.SFFReader(uri);
        iter = reader.keySet().iterator();
    }

    @Override
    public void delete() {
        new File(file).delete();
    }

    @Override
    public Set<DNAQualitySequenceHolder> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceHolder> ret = new HashSet<>();
        for (long id : ids) {
            String name = String.valueOf(id);
            String dna;
            try {
                dna = reader.getRead(name);
            } catch (IOException ex) {
                throw new SeqStoreException(ex.getMessage());
            }
            QualityDNASequence seq = new QualityDNASequence();
            seq.setName(name.getBytes());
            seq.setSequence(dna.getBytes());
            try {
                seq.setQuality(reader.getQuality(name));
            } catch (IOException ex) {
                throw new SeqStoreException(ex.getMessage());
            }
            DNAQualitySequenceHolder h = new DNAQualitySequenceHolder(seq);
            ret.add(h);
        }
        return ret;
    }

    @Override
    public boolean hasMoreElements() {
        boolean ret = iter.hasNext();
        String name = iter.next();

        QualityDNASequence seq = null;
        try {
            seq = new QualityDNASequence();
            seq.setName(name.getBytes());
            seq.setSequence(reader.getRead(name).getBytes());
            seq.setQuality(reader.getQuality(name));
        } catch (IOException ex) {
            Logger.getLogger(SFFReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        holder = new DNAQualitySequenceHolder(seq);
        return ret && seq != null;
    }

    private DNAQualitySequenceHolder holder = null;

    @Override
    public DNAQualitySequenceHolder nextElement() {
        DNAQualitySequenceHolder ret = holder;
        holder = null;
        return ret;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }

}
