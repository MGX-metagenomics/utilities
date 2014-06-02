/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqReaderI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.util.Set;

/**
 *
 * @author sj
 */
public interface SequenceAccessI extends AccessBaseI<SequenceI> {

    public void fetchSeqData(Iterable<SequenceI> sequences);

    public DownloadBaseI createDownloaderByAttributes(Set<AttributeI> value, SeqWriterI<DNASequenceI> writer, boolean closeWriter);

    public UploadBaseI createUploader(long seqrun_id, SeqReaderI<DNASequenceI> reader);

    public DownloadBaseI createDownloader(long seqrun_id, SeqWriterI<DNASequenceI> writer, boolean closeWriter);
}
