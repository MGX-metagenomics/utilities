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

    public static String translate(String dna) {
        return dna;
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
