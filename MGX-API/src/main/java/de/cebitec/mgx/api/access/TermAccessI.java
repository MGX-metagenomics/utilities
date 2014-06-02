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

    public Collection<TermI> byCategory(String category);

}
