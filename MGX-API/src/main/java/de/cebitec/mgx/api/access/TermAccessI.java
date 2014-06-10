/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.model.TermI;
import java.util.Collection;

/**
 *
 * @author sj
 */
public interface TermAccessI {

    // ontology lookup categories
    public static final String SEQ_METHODS = "seq_methods";
    public static final String SEQ_PLATFORMS = "seq_platforms";

    public Collection<TermI> byCategory(String category);

}
