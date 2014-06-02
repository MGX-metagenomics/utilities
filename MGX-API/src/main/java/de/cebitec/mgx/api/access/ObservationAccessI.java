/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.model.ObservationI;
import de.cebitec.mgx.api.model.SequenceI;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface ObservationAccessI {

    public Iterator<ObservationI> ByRead(SequenceI seq);
    
}
