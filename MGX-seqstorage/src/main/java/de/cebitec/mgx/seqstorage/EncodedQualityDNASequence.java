/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqcompression.FourBitEncoder;
import de.cebitec.mgx.seqcompression.QualityEncoder;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.internal.DNASequenceValidator;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import java.util.Arrays;

/**
 *
 * @author sj
 */
public class EncodedQualityDNASequence extends EncodedDNASequence implements DNAQualitySequenceI {

    private final byte[] encQual;

    public EncodedQualityDNASequence(long seqid, byte[] encodeddna, byte[] encodedqual) throws SequenceException {
        this(seqid, encodeddna, encodedqual, true);
    }

    public EncodedQualityDNASequence(long seqid, byte[] encodeddna, byte[] encodedqual, boolean enableValidation) throws SequenceException {
        super(seqid, encodeddna, enableValidation);

        encQual = Arrays.copyOf(encodedqual, encodedqual == null ? 0 : encodedqual.length);

        if (enableValidation) {

            if (encodedqual == null || encodedqual.length < 2) {
                throw new SequenceException("Invalid quality string");
            }
            // need to decode for validation
            long seqlen = FourBitEncoder.decodeLength(getEncodedSequence());
            byte[] decodedQual = QualityEncoder.decode(encodedqual, (int) seqlen);
            DNASequenceValidator.validateQuality(decodedQual);

            if (seqlen != decodedQual.length) {
                throw new SequenceException("DNA sequence and quality score length mismatch");
            }
        }
    }

    public byte[] getEncodedQuality() {
        return Arrays.copyOf(encQual, encQual.length);
    }

    @Override
    public byte[] getQuality() throws SequenceException {
        long len = FourBitEncoder.decodeLength(getEncodedSequence());
        if (len > 0) {
            return QualityEncoder.decode(encQual, (int) len);
        }
        return new byte[]{};
    }
}
