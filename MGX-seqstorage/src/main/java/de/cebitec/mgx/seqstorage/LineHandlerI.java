
package de.cebitec.mgx.seqstorage;

/**
 *
 * @author sjaenick
 */
public interface LineHandlerI {

    public void handle(byte[] buf, int start, int end);
}
