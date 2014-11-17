package de.cebitec.mgx.seqholder;

import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.sequence.DNASequenceI;

/**
 *
 * @author sjaenick
 */
public interface ReadSequenceI<T extends DNASequenceI> {

    public T getSequence();

    public SequenceDTO toDTO();
}
