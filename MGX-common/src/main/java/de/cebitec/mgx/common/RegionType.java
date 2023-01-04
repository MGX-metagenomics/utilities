package de.cebitec.mgx.common;

/**
 *
 * @author sjaenick
 */
public enum RegionType {

    CDS(0, "CDS"),
    RRNA(1, "rRNA"),
    TRNA(2, "tRNA"),
    TMRNA(3, "tmRNA"),
    NCRNA(4, "ncRNA"),
    MISC(5, "misc");
    

    private final int code;
    private final String s;

    private RegionType(int c, String text) {
        code = c;
        s = text;
    }

    public int getValue() {
        return code;
    }

    @Override
    public String toString() {
        return s;
    }
}
