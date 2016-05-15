package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.*;
import de.cebitec.mgx.sequence.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author Patrick Blumenkamp
 */
public class CSQFWriter implements SeqWriterI<DNAQualitySequenceI> {

    private final OutputStream seqout;
    private final OutputStream nameout;
    private long seqout_offset;
    private final String fname;

    public CSQFWriter(File file) throws IOException, SeqStoreException {
        this(file.getCanonicalPath());
    }

    public CSQFWriter(String filename) throws IOException, SeqStoreException {
        // make sure we don't accidentally overwrite pre-existing data
        if ((new File(filename + ".csq").exists()) || (new File(filename).exists())) {
            throw new SeqStoreException("CSQ file already exists");
        }

        fname = filename;

        seqout = new BufferedOutputStream(new FileOutputStream(filename + ".csq", false));
        seqout.write(FileMagic.CSQ_MAGIC);
        seqout_offset = FileMagic.CSQ_MAGIC.length;

        nameout = new BufferedOutputStream(new FileOutputStream(filename, false));
        nameout.write(FileMagic.NMS_MAGIC);
    }

    @Override
    public void addSequence(DNAQualitySequenceI seq) throws SeqStoreException {
        try {
            // save sequence id and offset
            byte[] id = ByteUtils.longToBytes(seq.getId());
            byte[] encoded_offset = ByteUtils.longToBytes(seqout_offset);
            nameout.write(id);
            nameout.write(encoded_offset);

            // encode sequence and write to seqout
            byte[] sequence = FourBitEncoder.encode(seq.getSequence());
            seqout.write(sequence);
            seqout.write(FourBitEncoder.RECORD_SEPARATOR);

            //write quality to seqout
            byte[] quality = QualityEncoder.encode(seq.getQuality());
            seqout.write(quality);

            // update offset
            seqout_offset += sequence.length;
            seqout_offset++; // separator char
            seqout_offset += quality.length;
        } catch (IOException ex) {
            throw new SeqStoreException(ex.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        seqout.close();
        nameout.close();

        Set<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_WRITE);
        try {
            Files.setPosixFilePermissions(Paths.get(fname), perms);
            Files.setPosixFilePermissions(Paths.get(fname + ".csq"), perms);
        } catch (UnsupportedOperationException uoex) {
            // not supported by underlying file system
        }

    }
}
