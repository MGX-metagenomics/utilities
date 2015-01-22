package de.cebitec.mgx.kegg.pathways.model;

import de.cebitec.mgx.kegg.pathways.KEGGException;
import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author sj
 */
public class ECNumberFactory {

    private final static ConcurrentMap<String, ECNumber> cache = new ConcurrentHashMap<>();

    public static ECNumberI fromString(String s) throws KEGGException {
        if (!cache.containsKey(s)) {
            if (s.split("[.]").length != 4) {
                throw new KEGGException("invalid EC number: " + s);
            }
            synchronized (cache) {
                if (!cache.containsKey(s)) {
                    cache.put(s, new ECNumber(s));
                }
            }
        }
        return cache.get(s);
    }
}
