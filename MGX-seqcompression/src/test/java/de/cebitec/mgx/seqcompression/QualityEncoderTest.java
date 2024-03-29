package de.cebitec.mgx.seqcompression;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Patrick Blumenkamp
 * <patrick.blumenkamp@computational.bio.uni-giessen.de>
 */
public class QualityEncoderTest {

    private static final Random rnd = new Random();

    /**
     * Test of decode and encode method on large quality array
     *
     * @throws de.cebitec.mgx.sequence.SeqStoreException No valid Sanger format
     */
    @Test
    public void testEncodeAndDecode_BigDataset() throws SequenceException {
        int seed = rnd.nextInt();
        rnd.setSeed(seed);
        System.out.println("Random seed: " + seed);
        byte[] quality = new byte[100_000];
        //must be set to guarantee maximal range in array
        quality[0] = 30;
        quality[1] = 43;
        for (int i = 2; i < quality.length; i++) {
            quality[i] = (byte) (rnd.nextInt(14) + 30);
        }
        byte[] encoded = QualityEncoder.encode(quality);
        assertEquals(2 + 50_000, encoded.length, "each quality should be 4 bits large");
        byte[] result = QualityEncoder.decode(encoded, quality.length);
        assertArrayEquals(quality, result);
    }

    /**
     * Test of decode and encode method on small quality array
     *
     * @throws de.cebitec.mgx.sequence.SeqStoreException No valid Sanger format
     */
    @Test
    public void testEncodeAndDecode_SmallDataset() throws SequenceException {
        byte[] quality = new byte[]{10, 12, 15, 13, 14, 17, 15, 20, 40};
        byte[] expResult = new byte[]{5, 9, 8, -52, 66, -96, -53, -8};
        byte[] encoded = QualityEncoder.encode(quality);
        assertArrayEquals(expResult, encoded);
        byte[] decoded = QualityEncoder.decode(encoded, quality.length);
        assertArrayEquals(quality, decoded);
    }

    @Test
    public void testEmptyQualityEncoding() throws SequenceException {
        byte[] quality = new byte[0];
        byte[] encoded = QualityEncoder.encode(quality);
        assertArrayEquals(new byte[]{0, 0}, encoded);
    }

    @Test
    public void testNullQualityEncoding() throws SequenceException {
        byte[] quality = null;
        byte[] encoded = QualityEncoder.encode(quality);
        assertArrayEquals(new byte[]{0, 0}, encoded);
    }

    @Test
    public void testEmptyQualityDecoding() throws SequenceException {
        byte[] encoded = new byte[]{0, 0};
        byte[] decoded = QualityEncoder.decode(encoded, 0);
        assertNotNull(decoded);
        assertEquals(0, decoded.length);
    }

    @Test
    public void testNullQualityDecoding() {
        byte[] quality = null;
        try {
            byte[] decoded = QualityEncoder.decode(quality, 0);
        } catch (SequenceException ex) {
            if (ex.getMessage().contains("Unable to decoded null or invalid data.")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail();
    }

}
