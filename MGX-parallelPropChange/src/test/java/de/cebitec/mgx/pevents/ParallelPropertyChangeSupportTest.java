/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.pevents;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import de.cebitec.mgx.pevents.test.EventTarget;
import de.cebitec.mgx.pevents.test.Sender;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class ParallelPropertyChangeSupportTest {

    public ParallelPropertyChangeSupportTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFoo() throws IOException {
        System.err.println("testTiming");

        for (int numRecvs = 1; numRecvs < 200; numRecvs++) {
            //System.err.println("num receivers "+ numRecvs);
            PropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object());

            List<EventTarget> recvs2 = new ArrayList<>();
            for (int i = 0; i < numRecvs; i++) {
                EventTarget r2 = new EventTarget(1);
                apcs.addPropertyChangeListener(r2);
                recvs2.add(r2);
            }

            long start2 = System.nanoTime();
            apcs.firePropertyChange("fpp", 1, 2);
            start2 = System.nanoTime() - start2;

            for (EventTarget r : recvs2) {
                apcs.removePropertyChangeListener(r);
            }

            //System.err.println("  sync notification completed after " + start + " ms");
        }
    }
    @Test
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
    public void testAllEventsReceived() {
        System.err.println("AllEventsReceived");
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport("AllEventsReceived");
        assertEquals(0, apcs.getPropertyChangeListeners().length);
        
        int cnt=25;

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
            System.err.println("recv cnt "+ r.getCount());
            assertEquals(1, r.getCount());
        }

    }
    @Test
    public void testRemove() {
        System.err.println("testRemove");
        Sender s = new Sender();
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
    public void testRemoveAfterSend() {
        System.err.println("testRemoveAfterSend");
        Sender s = new Sender();
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
    public void testTwoEvents() {
        System.err.println("twoEvents");
        Sender s = new Sender();
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
    public void testDestinations() {
        System.err.println("Destinations");
        Sender s1 = new Sender();
        Sender s2 = new Sender();

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
    public void testMultiple() {
        System.err.println("multipleEvents");
        Sender s1 = new Sender();

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
        assertEquals(5, r1.getCount());
    }
    @Test
    public void testCompareToPCS() {
        System.err.println("compareToPCS");

        EventTarget r1 = new EventTarget(1);
        EventTarget r2 = new EventTarget(1);
        PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object());

        pcs.addPropertyChangeListener(r1);
        pcs.addPropertyChangeListener(r1);

        apcs.addPropertyChangeListener(r2);
        apcs.addPropertyChangeListener(r2);

        assertEquals(2, pcs.getPropertyChangeListeners().length);
        assertEquals(2, apcs.getPropertyChangeListeners().length);

        pcs.firePropertyChange("foo", 1, 2);
        apcs.firePropertyChange("foo", 1, 2);

        assertEquals(2, r1.getCount());
        assertEquals(2, r2.getCount());
    }
    @Test
    public void testCompareToPCS2() {
        System.err.println("compareToPCS2");

        EventTarget r1 = new EventTarget(1);
        EventTarget r2 = new EventTarget(1);
        PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
        ParallelPropertyChangeSupport apcs = new ParallelPropertyChangeSupport(new Object());

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
