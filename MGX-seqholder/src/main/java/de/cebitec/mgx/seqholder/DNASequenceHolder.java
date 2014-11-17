package de.cebitec.mgx.seqholder;

import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.sequence.DNASequenceI;

public class DNASequenceHolder implements ReadSequenceI<DNASequenceI> {

    private final DNASequenceI seq;

    public DNASequenceHolder(DNASequenceI seq) {
        this.seq = seq;
    }

    @Override
    public DNASequenceI getSequence() {
        return seq;
    }

    @Override
    public SequenceDTO toDTO() {
        return SequenceDTO.newBuilder()
                .setName(new String(seq.getName()))
                .setSequence(new String(seq.getSequence()))
                .build();
    }
}
