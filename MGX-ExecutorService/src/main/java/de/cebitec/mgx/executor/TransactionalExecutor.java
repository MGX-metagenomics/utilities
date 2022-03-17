/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.executor;

/**
 *
 * @author
 * http://www.adam-bien.com/roller/abien/entry/conveniently_transactionally_and_legally_starting
 */
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import java.util.concurrent.Executor;

@Stateless
public class TransactionalExecutor implements Executor {

    @Override
    @Asynchronous
    public void execute(Runnable command) {
        command.run();
    }
}
