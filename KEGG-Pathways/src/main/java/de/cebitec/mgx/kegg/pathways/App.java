package de.cebitec.mgx.kegg.pathways;

import de.cebitec.mgx.kegg.pathways.api.ECNumberI;
import de.cebitec.mgx.kegg.pathways.api.PathwayI;
import de.cebitec.mgx.kegg.pathways.paint.KEGGPanel;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws KEGGException {
        final KEGGMaster m = KEGGMaster.getInstance();

        KEGGPanel panel = new KEGGPanel(m);
        //        List<ECNumberI> ecs = new ArrayList<>();
        //        ecs.add(ECNumberFactory.fromString("1.2.3.4"));
        //        ecs.add(ECNumberFactory.fromString("1.2.-.-"));
        //        ecs.add(ECNumberFactory.fromString("2.2.3.4"));
        //        ecs.add(ECNumberFactory.fromString("5.3.2.1"));
        //        Collections.sort(ecs);
        //        for (ECNumberI e : ecs) {
        //            System.err.println(e.getNumber());
        //        }
        Set<PathwayI> all = m.Pathways().fetchall();
        final CountDownLatch latch = new CountDownLatch(all.size());
        
        for (final PathwayI p : all) {
            System.err.println(p.getName());
            Map<ECNumberI, Set<Rectangle>> coords = m.Pathways().getCoords(p);
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        m.Pathways().getCoords(p);
                    } catch (KEGGException ex) {
                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        latch.countDown();
                    }
                }
            });
            t.setName(p.getMapNum());
            t.start();

            //if (p.getMapNum().equals("map00360")) {
              //  m.Pathways().getCoords(p);
//                JFrame frame = new JFrame(p.getMapNum());
//                panel.setPathway(p, 1);
//                for (ECNumberI e : ecs) {
//                    panel.addData(0, e, Color.BLUE, "42");
//                }
//                frame.getContentPane().add(panel);
//                frame.setSize(panel.getPreferredSize().width, panel.getPreferredSize().height + 20);
//                frame.setVisible(true);
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                break;
           // }
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
