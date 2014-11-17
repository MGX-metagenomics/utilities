package de.cebitec.mgx.seqholder;

import com.google.protobuf.ByteString;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;

/**
 *
 * @author sjaenick
 */
public class DNAQualitySequenceHolder implements ReadSequenceI<DNAQualitySequenceI> {

    private final DNAQualitySequenceI seq;

    public DNAQualitySequenceHolder(DNAQualitySequenceI seq) {
        this.seq = seq;
    }

    @Override
    public DNAQualitySequenceI getSequence() {
        return seq;
    }

    @Override
    public SequenceDTO toDTO() {
        return SequenceDTO.newBuilder()
                .setName(new String(seq.getName()))
                .setSequence(new String(seq.getSequence()))
                .setQuality(ByteString.copyFrom(seq.getQuality()))
                .build();
    }
}
