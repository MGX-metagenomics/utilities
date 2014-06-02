package de.cebitec.mgx.api.access;

import de.cebitec.mgx.api.misc.TaskI;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public interface AccessBaseI<T> {

    public long create(T obj);

    public T fetch(long id);

    public Iterator<T> fetchall();

    public void update(T obj);

    public TaskI delete(T obj);

}
