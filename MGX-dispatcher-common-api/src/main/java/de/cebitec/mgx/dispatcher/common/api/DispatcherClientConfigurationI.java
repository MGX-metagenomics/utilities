/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.dispatcher.common.api;

/**
 *
 * @author sj
 */
public interface DispatcherClientConfigurationI {

    String getDispatcherHost() throws MGXDispatcherException;

    String getDispatcherToken() throws MGXDispatcherException;
    
}
