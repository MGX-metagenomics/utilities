package de.cebitec.mgx.seqstorage.encoding;

/**
 *
 * @author sjaenick
 */
public class IUPACCodes {

    public static final byte A = 0x01;
    public static final byte T = 0x02;
//public final byte U 0x02
    public static final byte G = 0x04;
    public static final byte C = 0x08;
    public static final byte R = A | G;
    public static final byte Y = C | T;
    public static final byte S = C | G;
    public static final byte W = T | A;
    public static final byte K = T | G;
    public static final byte M = C | A;
    public static final byte B = C | G | T;
    public static final byte D = A | G | T;
    public static final byte H = A | C | T;
    public static final byte V = A | C | G;
    public static final byte N = A | T | C | G;
    
    public static final byte[] decoder = {'-', 'a', 't', 'w', 'g', 'r', 'k', 'd', 'c', 'm', 'y', 'h', 's', 'v', 'b', 'n'};
//    <Be-El> static readonly char[] BITS2CHAR = new char[] { '-', 'a', 't', 'w', 'g', 'r', 'k', 'd', 'c', 'm',
//<Be-El>         'y', 'h', 's', 'v', 'b', 'n' };
}
