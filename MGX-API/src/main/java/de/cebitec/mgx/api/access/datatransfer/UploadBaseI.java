package de.cebitec.mgx.api.access.datatransfer;


/**
 *
 * @author sj
 */
public abstract class UploadBaseI extends TransferBaseI {

    private CallbackI cb = null;
    private String error_message = "";
    protected static int DEFAULT_CHUNK_SIZE = 2048;
    protected int chunk_size = DEFAULT_CHUNK_SIZE;


    public void setChunkSize(int i) {
        chunk_size = i;
    }
    
    public int getChunkSize() {
        return chunk_size;
    }

//    protected PropertyChangeSupport getPropertyChangeSupport() {
//        return pcs;
//    }

    protected void abortTransfer(String reason, long total) {
        setErrorMessage(reason);
        fireTaskChange(TransferBaseI.NUM_ELEMENTS_SENT, total);
    }

    public String getErrorMessage() {
        return error_message;
    }

    protected void setErrorMessage(String msg) {
        error_message = msg;
    }

    public void setProgressCallback(CallbackI cb) {
        this.cb = cb;
    }

    protected CallbackI getProgressCallback() {
        return cb != null ? cb
                : new NullCallBack();
    }

    public abstract boolean upload();

    public abstract long getNumElementsSent();

    private final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }
}