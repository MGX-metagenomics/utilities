package de.cebitec.mgx.dispatcher.common.api;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXDispatcherException extends Exception {

    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

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
