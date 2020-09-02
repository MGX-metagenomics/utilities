/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNAQualitySequenceI;
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
public class SFFReader implements SeqReaderI<DNAQualitySequenceI> {

    private de.cebitec.mgx.sffreader.SFFReader reader = null;
    private final String file;
    private DNAQualitySequenceI holder = null;

    public SFFReader(String uri) throws SeqStoreException {
        this(uri, false);
    }

    public SFFReader(String uri, boolean is_compressed) throws SeqStoreException {
        if (is_compressed) {
            throw new SeqStoreException("Compressed SFF is not supported.");
        }
        this.file = uri;

    }

    @Override
    public void delete() {
        new File(file).delete();
    }

    @Override
    public Set<DNAQualitySequenceI> fetch(long[] ids) throws SeqStoreException {
        Set<DNAQualitySequenceI> res = new HashSet<>(ids.length);
        Set<Long> idList = new HashSet<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            idList.add(ids[i]);
        }
        while (hasMoreElements() && !idList.isEmpty()) {
            DNAQualitySequenceI elem = nextElement();
            if (idList.contains(elem.getId())) {
                res.add(elem);
                idList.remove(elem.getId());
            }
        }

        if (!idList.isEmpty()) {
            throw new SeqStoreException("Could not retrieve all sequences.");
        }
        return res;
    }

    @Override
    public boolean hasMoreElements() throws SeqStoreException {

        if (holder != null) {
            return true;
        }
        
        if (reader == null) {
            try {
                reader = new de.cebitec.mgx.sffreader.SFFReader(file);
            } catch (IOException ex) {
                throw new SeqStoreException("File not found or unreadable: " + file + "\n" + ex.getMessage());
            }
        }

        if (reader.hasMoreElements()) {
            SFFRead read = reader.nextElement();

            QualityDNASequence seq;

            seq = new QualityDNASequence();
            seq.setName(read.getName().getBytes());
            seq.setSequence(read.getBases().getBytes());
            seq.setQuality(read.getQuality());

            holder = seq;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final DNAQualitySequenceI nextElement() {
        DNAQualitySequenceI ret = holder;
        holder = null;
        return ret;
    }

    @Override
    public final void close() throws SeqStoreException {
        try {
            if (reader == null) {
                reader.close();
                reader = null;
            }
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
    }

    @Override
    public final boolean hasQuality() {
        return true;
    }
}
