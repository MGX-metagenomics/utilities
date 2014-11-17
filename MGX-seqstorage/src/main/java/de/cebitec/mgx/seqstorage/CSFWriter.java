package de.cebitec.mgx.seqstorage;

import de.cebitec.mgx.seqstorage.encoding.ByteUtils;
import de.cebitec.mgx.seqstorage.encoding.FourBitEncoder;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class CSFWriter implements SeqWriterI<DNASequenceI> {

    private OutputStream seqout;
    private OutputStream nameout;
    private long seqout_offset;
    private final String fname;

    public CSFWriter(File file) throws IOException, SeqStoreException {
        this(file.getCanonicalPath());
//
//        // make sure we don't accidentally overwrite pre-existing data
//        if ((new File(file.getCanonicalPath() + ".csf").exists()) || file.getCanonicalFile().exists()) {
//            throw new SeqStoreException("CSF file already exists");
//        }
//
//        fname = file.getCanonicalPath();
//
//        seqout = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath() + ".csf", false));
//        seqout.write(FourBitEncoder.CSF_MAGIC);
//        seqout_offset = FourBitEncoder.CSF_MAGIC.length;
//
//        nameout = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), false));
//        nameout.write(FourBitEncoder.NMS_MAGIC);
    }

    public CSFWriter(String filename) throws IOException, SeqStoreException {

        // make sure we don't accidentally overwrite pre-existing data
        if ((new File(filename + ".csf").exists()) || (new File(filename).exists())) {
            throw new SeqStoreException("CSF file already exists");
        }

        fname = filename;

        seqout = new BufferedOutputStream(new FileOutputStream(filename + ".csf", false));
        seqout.write(FourBitEncoder.CSF_MAGIC);
        seqout_offset = FourBitEncoder.CSF_MAGIC.length;

        nameout = new BufferedOutputStream(new FileOutputStream(filename, false));
        nameout.write(FourBitEncoder.NMS_MAGIC);
    }

    @Override
    public synchronized void addSequence(DNASequenceI seq) throws IOException {

        // save sequence id and offset
        byte[] id = ByteUtils.longToBytes(seq.getId());
        byte[] encoded_offset = ByteUtils.longToBytes(seqout_offset);
        nameout.write(id);
        nameout.write(encoded_offset);

        // encode sequence and write to seqout
        byte[] encoded = FourBitEncoder.encode(seq.getSequence());
        seqout.write(encoded);
        seqout.write(FourBitEncoder.RECORD_SEPARATOR);

        // update offset
        seqout_offset += encoded.length;
        seqout_offset++; // separator char
    }

    @Override
    public void close() throws IOException {
        seqout.close();
        nameout.close();

        Set<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_WRITE);
        Files.setPosixFilePermissions(Paths.get(fname), perms);
        Files.setPosixFilePermissions(Paths.get(fname + ".csf"), perms);
    }
}
