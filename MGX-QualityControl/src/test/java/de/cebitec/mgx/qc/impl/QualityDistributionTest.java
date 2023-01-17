package de.cebitec.mgx.qc.impl;

import de.cebitec.mgx.qc.DataRowI;
import de.cebitec.mgx.qc.QCResult;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.QualityDNASequence;
import de.cebitec.mgx.sequence.DNAQualitySequenceI;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Patrick Blumenkamp
 */
public class QualityDistributionTest {

    FirstReadQualityDistribution distribution;

    public QualityDistributionTest() {
    }

    @BeforeEach
    public void setUp() {
        distribution = new FirstReadQualityDistribution();
    }

    @Test
    public void testSimpleMean() throws SequenceException {
        System.out.println("testSimpleMean");
        byte[] qual1 = new byte[2_000];
        Arrays.fill(qual1, 0, 1000, (byte) 20);
        Arrays.fill(qual1, 1000, 2000, (byte) 10);

        byte[] dna = new byte[2_000];
        Arrays.fill(dna, (byte) 'A');
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna, qual1);

        distribution.add(qualitySequence1);

        QCResult calcResult = distribution.get();
        assertNotNull(calcResult);

        DataRowI mean = calcResult.getData()[0];
        float[] data = mean.getData();
        assertEquals(20, data[0]);
        assertEquals(10, data[1000]);
    }

    @Test
    public void testSimpleStdDev() throws SequenceException {
        System.out.println("testSimpleStdDev");
        byte[] qual1 = new byte[2_000];
        Arrays.fill(qual1, 0, 1000, (byte) 20);
        Arrays.fill(qual1, 1000, 2000, (byte) 10);

        byte[] dna = new byte[2_000];
        Arrays.fill(dna, (byte) 'A');
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna, qual1);

        distribution.add(qualitySequence1);

        QCResult calcResult = distribution.get();
        assertNotNull(calcResult);

        DataRowI stddev = calcResult.getData()[1];
        float[] data = stddev.getData();
        assertEquals(0, data[0]);
        assertEquals(0, data[1000]);
    }

    @Test
    public void testMean() throws SequenceException {
        System.out.println("testMean");
        byte[] qual1 = new byte[10];
        Arrays.fill(qual1, 0, 10, (byte) 20);
        byte[] dna1 = new byte[10];
        Arrays.fill(dna1, (byte) 'A');
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna1, qual1);

        byte[] qual2 = new byte[10];
        Arrays.fill(qual2, 0, 10, (byte) 30);
        byte[] dna2 = new byte[10];
        Arrays.fill(dna2, (byte) 'A');
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence(dna2, qual2);

        distribution.add(qualitySequence1);
        distribution.add(qualitySequence2);

        QCResult res = distribution.get();
        assertNotNull(res);

        DataRowI[] data = res.getData();
        assertNotNull(data);
        assertEquals(2, data.length);

        DataRowI d1 = data[0];
        assertEquals("mean", d1.getName());

        float[] means = d1.getData();
        assertNotNull(means);
        assertEquals(10, means.length);

        // phred 22.56 as mean of phread 20 and phred 30
        assertEquals(22.596, means[0], 0.001);

        DataRowI d2 = data[1];
        assertEquals("standard deviation", d2.getName());
        float[] stddevs = d2.getData();
        assertNotNull(stddevs);
        assertEquals(10, stddevs.length);
        assertEquals(0.006, stddevs[0], 0.001);
    }

    @Test
    public void testUpperBorder() throws SequenceException {
        System.out.println("testUpperBorder");
        byte[] qual1 = new byte[2_000];
        Arrays.fill(qual1, 0, 1000, (byte) 11);
        Arrays.fill(qual1, 1000, 2000, (byte) 12);
        byte[] dna = new byte[2_000];
        Arrays.fill(dna, (byte) 'A');
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna, qual1);

        byte[] qual2 = new byte[2_000];
        Arrays.fill(qual2, 0, 1000, (byte) 13);
        Arrays.fill(qual2, 1000, 2000, (byte) 14);
        byte[] dna2 = new byte[2_000];
        Arrays.fill(dna2, (byte) 'T');
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence(dna2, qual2);

        for (int i = 0; i < 10; i++) {
            distribution.add(qualitySequence1);
            distribution.add(qualitySequence2);
        }
        QCResult calcResult = distribution.get();

        float[] estMean = new float[2000];
        Arrays.fill(estMean, 0, 1000, 11.88f);
        Arrays.fill(estMean, 1000, 2000, 12.88f);
        float[] estStdDev = new float[2000];
        Arrays.fill(estStdDev, 0, 1000, 0.015f);
        Arrays.fill(estStdDev, 1000, 2000, 0.0119f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }

    @Test
    public void testExtendArrays() throws SequenceException {
        System.out.println("testExtendArrays");
        byte[] dna = new byte[5_000];
        Arrays.fill(dna, (byte) 'A');
        byte[] qual1 = new byte[5_000];
        Arrays.fill(qual1, 0, 3000, (byte) 17);
        Arrays.fill(qual1, 3000, 5000, (byte) 16);
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna, qual1);

        byte[] dna2 = new byte[6_000];
        Arrays.fill(dna2, (byte) 'A');
        byte[] qual2 = new byte[6_000];
        Arrays.fill(qual2, 0, 3000, (byte) 15);
        Arrays.fill(qual2, 3000, 6000, (byte) 14);
        DNAQualitySequenceI qualitySequence2 = new QualityDNASequence(dna2, qual2);

        for (int i = 0; i < 10_000; i++) {
            distribution.add(qualitySequence1);
            distribution.add(qualitySequence2);
        }
        QCResult calcResult = distribution.get();

        float[] estMean = new float[6000];
        Arrays.fill(estMean, 0, 3000, 15.88f);
        Arrays.fill(estMean, 3000, 5000, 14.88f);
        Arrays.fill(estMean, 5000, 6000, 14f);
        float[] estStdDev = new float[6000];
        Arrays.fill(estStdDev, 0, 3000, 0.0058f);
        Arrays.fill(estStdDev, 3000, 5000, 0.007f);
        Arrays.fill(estStdDev, 5000, 6000, 0.0f);
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }

    @Test
    public void testOnSingleSequence() throws SequenceException {
        System.out.println("testOnSingleSequence");

        byte[] dna = new byte[]{(byte) 'a'};
        byte[] qual = new byte[]{(byte) 5};
        DNAQualitySequenceI qualitySequence1 = new QualityDNASequence(dna, qual);

        distribution.add(qualitySequence1);

        QCResult calcResult = distribution.get();

        float[] estMean = new float[]{5f};
        assertArrayEquals(estMean, calcResult.getData()[0].getData(), 0.01f);

        float[] estStdDev = new float[]{0f};
        assertArrayEquals(estStdDev, calcResult.getData()[1].getData(), 0.01f);
    }
}
