/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public interface SampleAccessI  extends AccessBaseI<SampleI> {
    
    public SampleI create(HabitatI habitat, Date collectionDate, String material, double temperature, int volume, String volUnit);

    public Iterator<SampleI> ByHabitat(long habitat_id);
    
}
