package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.seqstorage.QualityDNASequence;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Patrick Blumenkamp
 */
public class QualityDistributionTest {

    FirstReadQualityDistribution distribution;

    public QualityDistributionTest() {
    }

    @Before
    public void setUp() {
        distribution = new FirstReadQualityDistribution();
    }

//    @Ignore("Test can take several minutes")
    @Test
    public void testUpperBorder() throws SeqStoreException {
        System.out.println("testUpperBorder");
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence();
        byte[] qual1 = new byte[2_000];
        Arrays.fill(qual1, 0, 1000, (byte) 127);
        Arrays.fill(qual1, 1000, 2000, (byte) 126);
        qualitySequence1.setQuality(qual1);
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence();
        byte[] qual2 = new byte[2_000];
        Arrays.fill(qual2, 0, 1000, (byte) 126);
        Arrays.fill(qual2, 1000, 2000, (byte) 123);
        qualitySequence2.setQuality(qual2);

        for (int i = 0; i < 80_000; i++) {
            distribution.add(qualitySequence1);
            distribution.add(qualitySequence2);
        }
        QCResult calcResult = distribution.get();

        float[] estMean = new float[2000];
        Arrays.fill(estMean, 0, 1000, 126.5f);
        Arrays.fill(estMean, 1000, 2000, 124.5f);
        float[] estStdDev = new float[2000];
        Arrays.fill(estStdDev, 0, 1000, 0.5f);
        Arrays.fill(estStdDev, 1000, 2000, 1.5f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }

    @Test
    public void testExtendArrays() throws SeqStoreException {
        System.out.println("testExtendArrays");
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence();
        byte[] qual1 = new byte[5_000];
        Arrays.fill(qual1, 0, 3000, (byte) 127);
        Arrays.fill(qual1, 3000, 5000, (byte) 126);
        qualitySequence1.setQuality(qual1);
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence();
        byte[] qual2 = new byte[6_000];
        Arrays.fill(qual2, 0, 3000, (byte) 126);
        Arrays.fill(qual2, 3000, 6000, (byte) 123);
        qualitySequence2.setQuality(qual2);

        for (int i = 0; i < 10_000; i++) {
            distribution.add(qualitySequence1);
            distribution.add(qualitySequence2);
        }
        QCResult calcResult = distribution.get();

        float[] estMean = new float[6000];
        Arrays.fill(estMean, 0, 3000, 126.5f);
        Arrays.fill(estMean, 3000, 5000, 124.5f);
        Arrays.fill(estMean, 5000, 6000, 123f);
        float[] estStdDev = new float[6000];
        Arrays.fill(estStdDev, 0, 3000, 0.5f);
        Arrays.fill(estStdDev, 3000, 5000, 1.5f);
        Arrays.fill(estStdDev, 5000, 6000, 0.0f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }

    @Test
    public void testCalculations() throws SeqStoreException {
        System.out.println("testCalculations");
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence();
        byte[] qual1 = new byte[1_000];
        Arrays.fill(qual1, 0, 500, (byte) 127);
        Arrays.fill(qual1, 500, 1000, (byte) 126);
        qualitySequence1.setQuality(qual1);
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence();
        byte[] qual2 = new byte[1_000];
        Arrays.fill(qual2, 0, 500, (byte) 126);
        Arrays.fill(qual2, 500, 1000, (byte) 123);
        qualitySequence2.setQuality(qual2);

        for (int i = 0; i < 1_000_000; i++) {
            distribution.add(qualitySequence1);
            distribution.add(qualitySequence2);
        }
        QCResult calcResult = distribution.get();

        float[] estMean = new float[1000];
        Arrays.fill(estMean, 0, 500, 126.5f);
        Arrays.fill(estMean, 500, 1000, 124.5f);
        float[] estStdDev = new float[1000];
        Arrays.fill(estStdDev, 0, 500, 0.5f);
        Arrays.fill(estStdDev, 500, 1000, 1.5f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }

    @Test
    public void testOnSingleSequence() throws SeqStoreException {
        System.out.println("testOnSingleSequence");
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence();
        byte[] qual1 = new byte[1_000];
        Arrays.fill(qual1, 0, 500, (byte) 127);
        Arrays.fill(qual1, 500, 1000, (byte) 126);
        qualitySequence1.setQuality(qual1);

        distribution.add(qualitySequence1);

        QCResult calcResult = distribution.get();

        float[] estMean = new float[1000];
        Arrays.fill(estMean, 0, 500, 127f);
        Arrays.fill(estMean, 500, 1000, 126f);
        float[] estStdDev = new float[1000];
        Arrays.fill(estStdDev, 0, 1000, 0f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }
}
