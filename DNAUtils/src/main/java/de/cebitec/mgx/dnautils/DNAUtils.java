/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.dnautils;

/**
 *
 * @author sj
 */
public class DNAUtils {

    // genetic code 11
    private final static String AAs = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG";
    private final static String Starts = "---M------**--*----M------------MMMM---------------M------------";
    private final static String Base1 = "TTTTTTTTTTTTTTTTCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGG";
    private final static String Base2 = "TTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGGTTTTCCCCAAAAGGGG";
    private final static String Base3 = "TCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAGTCAG";

    private static char codonToAA(String codon) {
        if (codon.length() != 3) {
            return 'X';
        }
        int i = 0;
        while (i < Base1.length()) {
            if (codon.charAt(0) == Base1.charAt(i)
                    && codon.charAt(1) == Base2.charAt(i)
                    && codon.charAt(2) == Base3.charAt(i)) {
                return AAs.charAt(i);
            }
            i++;
        }
        return 'X';
    }

    public static String translate(String dna) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dna.length() - 2; i += 3) {
            String codon = dna.substring(i, i + 3);
            builder.append(codonToAA(codon));
        }
        return builder.toString();
    }

    public static String reverseComplement(String dna) {
        StringBuilder builder = new StringBuilder();

        for (int i = dna.length() - 1; i >= 0; i--) {
            if (dna.charAt(i) == 'T') {
                builder.append('A');
            }
            if (dna.charAt(i) == 'A') {
                builder.append('T');
            }
            if (dna.charAt(i) == 'C') {
                builder.append('G');
            }
            if (dna.charAt(i) == 'G') {
                builder.append('C');
            }
        }
        return builder.toString();
    }

    public static String complement(String dna) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < dna.length(); i++) {
            if (dna.charAt(i) == 'T') {
                builder.append('A');
            }
            if (dna.charAt(i) == 'A') {
                builder.append('T');
            }
            if (dna.charAt(i) == 'C') {
                builder.append('G');
            }
            if (dna.charAt(i) == 'G') {
                builder.append('C');
            }
        }
        return builder.toString();
    }
}
