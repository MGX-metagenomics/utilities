/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.internal.DNASequenceValidator;
import de.cebitec.mgx.sequence.DNASequenceI;
import java.util.Arrays;

/**
 *
 * @author sj
 */
public class EncodedDNASequence implements DNASequenceI {

    private long id;
    private byte[] name = null;
    private final byte[] encodedDNA;

    public EncodedDNASequence(long seqid, byte[] encseq) throws SequenceException {
        this(seqid, encseq, true);
    }

    public EncodedDNASequence(long seqid, byte[] encseq, boolean enableValidation) throws SequenceException {
        id = seqid;
        encodedDNA = Arrays.copyOf(encseq, encseq.length);
        if (enableValidation) {
            DNASequenceValidator.validateSequence(FourBitEncoder.decode(encseq));
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public byte[] getName() {
        return name;
    }

    @Override
    public void setName(byte[] seqname) {
        name = Arrays.copyOf(seqname, seqname.length);
    }

    public byte[] getEncodedSequence() {
        return Arrays.copyOf(encodedDNA, encodedDNA.length);
    }

    @Override
    public final byte[] getSequence() throws SequenceException {
        return FourBitEncoder.decode(encodedDNA);
    }

}
