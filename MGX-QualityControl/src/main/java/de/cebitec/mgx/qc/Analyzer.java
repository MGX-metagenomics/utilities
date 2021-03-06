/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.qc;

import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.sequence.DNASequenceI;

/**
 *
 * @author sj
 */
public interface Analyzer<T extends DNASequenceI> {

    public String getName();

    public String getDescription();

    public void add(T seq) throws SequenceException;

    public void addPair(T seq1, T seq2) throws SequenceException;

    public QCResult get();

    public long getNumberOfSequences();

}
