package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;

/**
 *
 * @author sjaenick
 */
public class DNASequence implements DNASequenceI {

    private byte[] name = null;
    private byte[] dnasequence = null;
    private long id;

    public DNASequence() {
    }

    public DNASequence(long seqid) {
        id = seqid;
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
        name = new byte[seqname.length];
        System.arraycopy(seqname, 0, name, 0, seqname.length);
    }

    @Override
    public final byte[] getSequence() {
        return dnasequence;
    }

    @Override
    public void setSequence(byte[] sequence) throws SeqStoreException {
        dnasequence = new byte[sequence.length];
        System.arraycopy(sequence, 0, dnasequence, 0, sequence.length);

        // validate nucleotide sequence, convert to uppercase if necessary
        for (int i = 0; i < dnasequence.length; i++) {
            switch (dnasequence[i]) {
                case 'A':
                case 'T':
                case 'G':
                case 'C':
                case 'R':
                case 'Y':
                case 'S':
                case 'W':
                case 'K':
                case 'M':
                case 'B':
                case 'D':
                case 'H':
                case 'V':
                case 'N':
                    break;
                case 'a':
                    dnasequence[i] = 'A';
                    break;
                case 't':
                    dnasequence[i] = 'T';
                    break;
                case 'g':
                    dnasequence[i] = 'G';
                    break;
                case 'c':
                    dnasequence[i] = 'C';
                    break;
                case 'r':
                    dnasequence[i] = 'R';
                    break;
                case 'y':
                    dnasequence[i] = 'Y';
                    break;
                case 's':
                    dnasequence[i] = 'S';
                    break;
                case 'w':
                    dnasequence[i] = 'W';
                    break;
                case 'k':
                    dnasequence[i] = 'K';
                    break;
                case 'm':
                    dnasequence[i] = 'M';
                    break;
                case 'b':
                    dnasequence[i] = 'B';
                    break;
                case 'd':
                    dnasequence[i] = 'D';
                    break;
                case 'h':
                    dnasequence[i] = 'H';
                    break;
                case 'v':
                    dnasequence[i] = 'V';
                    break;
                case 'n':
                    dnasequence[i] = 'N';
                    break;
                default:
                    throw new SeqStoreException("Illegal nucleotide " + dnasequence[i] + " at position " + i + " of sequence " + new String(getName()));
            }
        }
    }
}
