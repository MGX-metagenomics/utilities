package de.cebitec.mgx.dispatcher.common.api;

import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class MGXInsufficientJobConfigurationException extends MGXDispatcherException {

    @Serial
    private static final long serialVersionUID = 6401253773779951803L;

    public MGXInsufficientJobConfigurationException(Throwable cause) {
        super(cause);
    }

    public MGXInsufficientJobConfigurationException(String msg) {
        super(msg);
    }
}
