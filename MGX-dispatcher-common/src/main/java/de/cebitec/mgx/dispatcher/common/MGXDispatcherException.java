package de.cebitec.mgx.dispatcher.common;

/**
 *
 * @author sjaenick
 */
public class MGXDispatcherException extends Exception {

    public MGXDispatcherException(Throwable cause) {
        super(cause);
    }

    public MGXDispatcherException(String msg) {
        super(msg);
    }

    public MGXDispatcherException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
