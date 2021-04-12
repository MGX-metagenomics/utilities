/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.seqcompression.QualityEncoder;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;

/**
 *
 * @author sj
 */
public class EncodedQualityDNASequence extends EncodedDNASequence implements DNAQualitySequenceI {

    private byte[] encQual;

    public byte[] getEncodedQuality() {
        return encQual;
    }

    public void setEncodedQuality(byte[] encoded) {
        this.encQual = encoded;
    }

    @Override
    public byte[] getQuality() throws SequenceException {
        long len = FourBitEncoder.decodeLength(getEncodedSequence());
        return QualityEncoder.decode(encQual, (int)len);
    }

    @Override
    public void setQuality(byte[] qual) throws SequenceException {
        encQual = QualityEncoder.encode(qual);
    }

}
