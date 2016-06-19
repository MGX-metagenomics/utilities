/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.sffreader.datatypes;

/**
 *
 * @author Patrick Blumenkamp
 */
public class SFFRead {

    private final String name, bases;
    private final byte[] quality;

    public SFFRead(String name, String bases, byte[] quality) {
        this.name = name;
        this.bases = bases;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public String getBases() {
        return bases;
    }

    public byte[] getQuality() {
        return quality.clone();
    }

}
