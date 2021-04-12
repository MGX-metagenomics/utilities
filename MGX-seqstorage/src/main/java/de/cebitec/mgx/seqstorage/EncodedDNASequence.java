/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.seqcompression.SequenceException;

/**
 *
 * @author sj
 */
public class EncodedDNASequence extends DNASequence {

    private byte[] encodedDNA;

    public byte[] getEncodedSequence() {
        return encodedDNA;
    }

    public void setEncodedSequence(byte[] enc) {
        this.encodedDNA = enc;
    }

    @Override
    public void setSequence(byte[] sequence) throws SequenceException {
        encodedDNA = FourBitEncoder.encode(sequence);
    }

    @Override
    public final byte[] getSequence() throws SequenceException {
        byte[] decoded = super.getSequence();
        if (decoded == null) {
            super.setSequence(FourBitEncoder.decode(encodedDNA));
        }
        return super.getSequence();
    }

}
