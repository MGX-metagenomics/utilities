package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public interface AccessBaseI<T> {

    public T create(T obj) throws MGXException;

    public T fetch(long id) throws MGXException;

    public Iterator<T> fetchall() throws MGXException;

    public void update(T obj) throws MGXException;

    public TaskI delete(T obj) throws MGXException;

}
