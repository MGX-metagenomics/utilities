/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.parallel.api;


/**
 *
 * @author sj
 */
public abstract class ParallelWorkerI<T, U> implements InputConsumerI<T>, OutputProviderI<U>, Runnable, Cloneable {

    public abstract void done();

    @Override
    public abstract ParallelWorkerI<T, U> clone(); // throws CloneNotSupportedException;
    
}
