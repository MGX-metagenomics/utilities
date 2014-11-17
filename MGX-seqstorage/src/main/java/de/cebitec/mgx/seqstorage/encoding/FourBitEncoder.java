package de.cebitec.mgx.seqstorage.encoding;

/**
 *
 * @author sjaenick
 */
public class FourBitEncoder {

    public static final byte[] CSF_MAGIC = {'C', 'S', 'F', '\n'};
    public static final byte[] NMS_MAGIC = {'N', 'M', 'S', '\n'};
    public static final byte RECORD_SEPARATOR = '\0';

    public static byte[] encode(byte[] seq) {
        int seq_len = seq.length;
        int encoded_len = seq_len / 2 + (seq_len & 1);

        byte[] encoded = new byte[encoded_len];

        byte bitmask;
        for (int i = 0; i < seq_len; i++) {

            switch (seq[i]) {
                case 'A':
                case 'a':
                    bitmask = IUPACCodes.A;
                    break;
                case 'T':
                case 't':
                    bitmask = IUPACCodes.T;
                    break;
                case 'C':
                case 'c':
                    bitmask = IUPACCodes.C;
                    break;
                case 'G':
                case 'g':
                    bitmask = IUPACCodes.G;
                    break;
                case 'R':
                case 'r':
                    bitmask = IUPACCodes.R;
                    break;
                case 'S':
                case 's':
                    bitmask = IUPACCodes.S;
                    break;
                case 'M':
                case 'm':
                    bitmask = IUPACCodes.M;
                    break;
                case 'B':
                case 'b':
                    bitmask = IUPACCodes.B;
                    break;
                case 'D':
                case 'd':
                    bitmask = IUPACCodes.D;
                    break;
                case 'H':
                case 'h':
                    bitmask = IUPACCodes.H;
                    break;
                case 'V':
                case 'v':
                    bitmask = IUPACCodes.V;
                    break;
                case 'N':
                case 'n':
                    bitmask = IUPACCodes.N;
                    break;
                /*
                case '\n':
                fprintf(stderr, "got line break\n", nucleotide);
                break;
                 */

                default:
                    //fprintf(stderr, "unknown char %c, using N\n", nucleotide);
                    bitmask = IUPACCodes.N;
                    break;
            }

            if ((i & 1) == 0) {
                bitmask <<= 4;
            }

            encoded[i >> 1] |= bitmask;
        }

        return encoded;
    }

    public static byte[] decode(byte[] enc) {

        int dec_len = enc.length * 2;
        // decoded sequence length is odd
        if ((enc[enc.length - 1] & 0x0F) == 0x0) {
            dec_len--;
        }
        byte[] decoded = new byte[dec_len];

        for (int dec_pos = 0; dec_pos < dec_len; dec_pos++) {
            byte enc_base = enc[dec_pos / 2];

            if ((dec_pos % 2) == 0) {
                //get first 4bits
                enc_base >>= 4;
            }
            // mask last 4 bits
            enc_base &= 0x0F;
            decoded[dec_pos] = IUPACCodes.decoder[(int) enc_base];
        }

        return decoded;
    }
}
