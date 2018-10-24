package net.bak3dnet.robotics.displays;

import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryTest {

    @Test
    public void testDChar() {

        DChar ch = DCharFactory.getDChar('A', false);
        assertEquals("The DChar should encapsulate A", ch.getEncapsulatedChar(), 'A');

    }

}