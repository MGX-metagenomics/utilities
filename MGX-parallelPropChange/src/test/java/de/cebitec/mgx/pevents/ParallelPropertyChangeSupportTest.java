/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents;

import de.cebitec.mgx.pevents.test.EventTarget;
import de.cebitec.mgx.pevents.test.Forwarder;
import de.cebitec.mgx.pevents.test.Sender;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 *
 * @author sj
 */
public class ParallelPropertyChangeSupportTest {

    public ParallelPropertyChangeSupportTest() {
    }

    @Test
    @Timeout(60)
    public void testDeadLock() {
        System.err.println("testRegressionDeadLock");
        Sender s = new Sender("testRegressionDeadLock");

        Forwarder fw = null;
        for (int i = 0; i < 5; i++) {
            Forwarder f = new Forwarder();
            if (fw == null) {
                s.addPropertyChangeListener(f);
            } else {
                fw.addPropertyChangeListener(f);
            }
            fw = f;
        }

        s.firePropertyChange();

        s.removePropertyChangeListener(fw);

        assertEquals(1, fw.getCount());
    }

    @Test
    @Timeout(60)
    public void testTiming() throws IOException {
        System.err.println("testTiming");

        for (int numRecvs = 1; numRecvs < 200; numRecvs++) {
            //System.err.println("num receivers "+ numRecvs);
            PropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object());
            assertEquals(0, apcs.getPropertyChangeListeners().length);

            List<EventTarget> recvs2 = new ArrayList<>();
            for (int i = 0; i < numRecvs; i++) {
                EventTarget r2 = new EventTarget(i);
                apcs.addPropertyChangeListener(r2);
                recvs2.add(r2);
            }
            System.err.println("  " + numRecvs + " listeners added");
            assertEquals(numRecvs, apcs.getPropertyChangeListeners().length);

            long start2 = System.nanoTime();
            apcs.firePropertyChange("fpp", 1, 2);
            System.err.println("   propChange done");
            start2 = System.nanoTime() - start2;

            for (EventTarget r : recvs2) {
                assertEquals(1, r.getCount());
                apcs.removePropertyChangeListener(r);
            }
            System.err.println("  listeners removed");
            assertEquals(0, apcs.getPropertyChangeListeners().length);

            //System.err.println("  sync notification completed after " + start + " ms");
        }
    }

    @Test
    @Timeout(60)
    public void testListenerCount() {
        System.err.println("listenerCount");
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport("listenerCount");

        assertEquals(0, apcs.getPropertyChangeListeners().length);
        List<EventTarget> recvs2 = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            EventTarget r = new EventTarget(1);
            apcs.addPropertyChangeListener(r);
            recvs2.add(r);
            assertEquals(i + 1, apcs.getPropertyChangeListeners().length);
        }

        assertEquals(25, apcs.getPropertyChangeListeners().length);

//        apcs.removePropertyChangeListener(recvs2.get(0));
//        assertEquals(24, apcs.getPropertyChangeListeners().length);
        for (EventTarget r : recvs2) {
            apcs.removePropertyChangeListener(r);
        }
        assertEquals(0, apcs.getPropertyChangeListeners().length);
    }

    @Test
    @Timeout(60)
    public void testAllEventsReceived() {
        System.err.println("AllEventsReceived");
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport("AllEventsReceived");
        assertEquals(0, apcs.getPropertyChangeListeners().length);

        int cnt = 25;

        List<EventTarget> recvs2 = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            EventTarget r = new EventTarget(1);
            apcs.addPropertyChangeListener(r);
            recvs2.add(r);
        }

        long start = System.currentTimeMillis();
        apcs.firePropertyChange("foo", 1, 2);
        start = System.currentTimeMillis() - start;
        System.err.println("  async notification completed after " + start + " ms");

        for (EventTarget r : recvs2) {
            apcs.removePropertyChangeListener(r);
        }

        for (EventTarget r : recvs2) {
            System.err.println("recv cnt " + r.getCount());
            assertEquals(1, r.getCount());
        }

    }

    @Test
    @Timeout(60)
    public void testRemove() {
        System.err.println("testRemove");
        Sender s = new Sender("testRemove");
        List<EventTarget> recvs2 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            EventTarget r = new EventTarget(1);
            s.addPropertyChangeListener(r);
            recvs2.add(r);
        }

        for (EventTarget r : recvs2) {
            s.removePropertyChangeListener(r);
        }

        PropertyChangeListener[] listeners = s.getPropertyChangeListeners();
        assertEquals(0, listeners.length);
    }

    @Test
    @Timeout(60)
    public void testRemoveAfterSend() {
        System.err.println("testRemoveAfterSend");
        Sender s = new Sender("testRemoveAfterSend");
        List<EventTarget> recvs2 = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            EventTarget r = new EventTarget(1);
            s.addPropertyChangeListener(r);
            recvs2.add(r);
        }

        s.firePropertyChange();

        for (EventTarget r : recvs2) {
            s.removePropertyChangeListener(r);
        }

        PropertyChangeListener[] listeners = s.getPropertyChangeListeners();
        assertEquals(0, listeners.length);
    }

    @Test
    @Timeout(60)
    public void testTwoEvents() {
        System.err.println("twoEvents");
        Sender s = new Sender("twoEvents");
        List<EventTarget> recvs2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EventTarget r = new EventTarget(1);
            s.addPropertyChangeListener(r);
            recvs2.add(r);
        }
        for (EventTarget r : recvs2) {
            assertEquals(0, r.getCount());
        }
        System.err.println("sending first event");
        s.firePropertyChange();
        for (EventTarget r : recvs2) {
            assertEquals(1, r.getCount());
        }
        System.err.println("sending second event");
        s.firePropertyChange();

        for (EventTarget r : recvs2) {
            assertEquals(2, r.getCount());
        }
        for (EventTarget r : recvs2) {
            s.removePropertyChangeListener(r);
        }
    }

    @Test
    @Timeout(60)
    public void testDestinations() {
        System.err.println("Destinations");
        Sender s1 = new Sender("Destinations1");
        Sender s2 = new Sender("Destinations2");

        EventTarget r1 = new EventTarget(1);
        s1.addPropertyChangeListener(r1);

        EventTarget r2 = new EventTarget(2);
        s1.addPropertyChangeListener(r2);
        s2.addPropertyChangeListener(r2);

        // fire both
        s1.firePropertyChange();
        s2.firePropertyChange();

        assertEquals(1, r1.getCount());
        assertEquals(2, r2.getCount());
    }

    @Test
    @Timeout(60)
    public void testMultiple() {
        System.err.println("multipleEvents");
        Sender s1 = new Sender("multipleEvents");

        EventTarget r1 = new EventTarget(1);
        s1.addPropertyChangeListener(r1);

        long start = System.nanoTime();
        s1.firePropertyChange();
        s1.firePropertyChange();
        s1.firePropertyChange();
        s1.firePropertyChange();
        s1.firePropertyChange();
        start = System.nanoTime() - start;
        System.err.println("  5 events sent after " + start + " ns");
        s1.removePropertyChangeListener(r1);
        assertEquals(5, r1.getCount());
    }

//    @Test
//    public void testCompareToPCSLoop() {
//        for (int i=0; i < 5000; i++) {
//            testCompareToPCS();
//        }
//    }
    @Test
    @Timeout(60)
    public void testCompareToPCS() {
        System.err.println("compareToPCS");

        EventTarget r1 = new EventTarget(1);
        EventTarget r2 = new EventTarget(1);
        PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object(), false);

        pcs.addPropertyChangeListener(r1);
        pcs.addPropertyChangeListener(r1);

        apcs.addPropertyChangeListener(r2);
        apcs.addPropertyChangeListener(r2);

        assertEquals(2, pcs.getPropertyChangeListeners().length);
        assertEquals(2, apcs.getPropertyChangeListeners().length);

        pcs.firePropertyChange("PCS", 1, 2);
        apcs.firePropertyChange("aPCS", 3, 4);

        assertEquals(2, r1.getCount());
        assertEquals(2, r2.getCount());
    }

    @Test
    @Timeout(60)
    public void testCompareToPCS2() {
        System.err.println("compareToPCS2");

        EventTarget r1 = new EventTarget(1);
        EventTarget r2 = new EventTarget(1);
        PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object(), false);

        pcs.addPropertyChangeListener(r1);
        pcs.addPropertyChangeListener(r1);
        pcs.removePropertyChangeListener(r1);

        apcs.addPropertyChangeListener(r2);
        apcs.addPropertyChangeListener(r2);
        apcs.removePropertyChangeListener(r2);

        assertEquals(1, pcs.getPropertyChangeListeners().length);
        assertEquals(1, apcs.getPropertyChangeListeners().length);

        pcs.firePropertyChange("foo", 1, 2);
        apcs.firePropertyChange("foo", 1, 2);

        assertEquals(1, r1.getCount());
        assertEquals(1, r2.getCount());
    }

    @Test
    @Timeout(60)
    public void testCompareToPCSNullSource() {
        System.err.println("compareToPCSNullSource");

        PropertyChangeSupport pcs = null;
        try {
            pcs = new PropertyChangeSupport(null);
        } catch (NullPointerException npe) {
            // ok
        }
        assertNull(pcs);

        ParallelPropertyChangeSupport apcs = null;

        try {
            apcs = new ParallelPropertyChangeSupport(null);
        } catch (NullPointerException npe) {
            // ok
        }
        assertNull(apcs);
    }
}
