package de.cebitec.mgx.common;

/**
 *
 * @author sjaenick
 */
public enum JobState {

    CREATED(0),
    VERIFIED(1),
    SUBMITTED(2),
    QUEUED(3),
    RUNNING(4),
    FINISHED(5),
    FAILED(6),
    ABORTED(7),
    IN_DELETION(8);
    private final int value;

    private JobState(int v) {
        value = v;
    }
};
