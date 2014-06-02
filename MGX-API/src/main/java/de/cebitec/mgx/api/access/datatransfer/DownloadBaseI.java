
package de.cebitec.mgx.api.access.datatransfer;

/**
 *
 * @author sjaenick
 */
public abstract class DownloadBaseI extends TransferBaseI {
    
    private CallbackI cb = null;
    private String error_message = "";

    protected void abortTransfer(String reason, long total) {
        setErrorMessage(reason);
        fireTaskChange(TransferBaseI.TRANSFER_FAILED, 1);
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
                : new DownloadBaseI.NullCallBack();
    }

    public abstract boolean download();

    public abstract long getProgress();

    protected final static class NullCallBack implements CallbackI {

        @Override
        public void callback(long i) {
        }
    }
}
