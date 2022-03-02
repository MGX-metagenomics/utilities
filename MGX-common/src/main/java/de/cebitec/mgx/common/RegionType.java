package de.cebitec.mgx.common;

/**
 *
 * @author sjaenick
 */
public enum RegionType {

    CDS(0),
    RRNA(1),
    TRNA(2),
    TMRNA(3),
    NCRNA(4),
    MISC(5);
    

    private final int code;

    private RegionType(int c) {
        code = c;
    }

    public int getValue() {
        return code;
    }
}
