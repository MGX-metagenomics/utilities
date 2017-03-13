package de.cebitec.mgx.dispatcher.common.api;

import de.cebitec.mgx.dispatcher.common.api.MGXDispatcherException;


/**
 *
 * @author sjaenick
 */
public class MGXInsufficientJobConfigurationException extends MGXDispatcherException {

    public MGXInsufficientJobConfigurationException(Throwable cause) {
        super(cause);
    }

    public MGXInsufficientJobConfigurationException(String msg) {
        super(msg);
    }
}
