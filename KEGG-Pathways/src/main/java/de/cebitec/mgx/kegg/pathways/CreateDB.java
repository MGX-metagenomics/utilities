package de.cebitec.mgx.kegg.pathways;

import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create new KEGG database
 *
 */
public class CreateDB {

    public static void main(String[] args) throws KEGGException {
        final KEGGMaster m = KEGGMaster.getInstance("/tmp/kegg/", true);

        // fetch and store pathway list from REST service
        m.Pathways().fetchAllFromServer();
        
        int i =1;
        
        
        for (final PathwayI p : m.Pathways().fetchall()) {
            System.err.println(p.getName());
            m.Pathways().fetchCoordsFromServer(p);
            System.err.println(++i);
            
            //
            // the KEGG rest service seems to have implemented some kind of
            // rate limit: if too many requests are being made, the server 
            // starts to return http status code 403 (forbidden)
            //
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        }
      
    }
}
