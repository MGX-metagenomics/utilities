/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sj
 */
public class AlternatingQReader implements SeqReaderI<DNAQualitySequenceI> {

    private final SeqReaderI<DNAQualitySequenceI> reader1;
    private final SeqReaderI<DNAQualitySequenceI> reader2;
    private boolean useFirst = true;

    public AlternatingQReader(SeqReaderI<DNAQualitySequenceI> reader1, SeqReaderI<DNAQualitySequenceI> reader2) {
        this.reader1 = reader1;
        this.reader2 = reader2;
    }

    @Override
    public void delete() {
        reader1.delete();
        reader2.delete();
    }

    @Override
    public Set<DNAQualitySequenceI> fetch(long[] ids) throws SequenceException {
        Set<DNAQualitySequenceI> ret = new HashSet<>();
        ret.addAll(reader1.fetch(ids));
        ret.addAll(reader2.fetch(ids));
        return ret;
    }

    @Override
    public boolean hasQuality() {
        return reader1.hasQuality() && reader2.hasQuality();
    }

    @Override
    public DNAQualitySequenceI nextElement() {
        if (useFirst) {
            useFirst = false;
            return reader1.nextElement();
        } else {
            useFirst = true;
            return reader2.nextElement();
        }
    }

    @Override
    public boolean hasMoreElements() throws SequenceException {
        return useFirst ? reader1.hasMoreElements() : reader2.hasMoreElements();
    }

    @Override
    public void close() throws SeqStoreException {
        reader1.close();
        reader2.close();
    }

}
