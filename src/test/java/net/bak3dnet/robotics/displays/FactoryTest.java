package net.bak3dnet.robotics.displays;

import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryTest {

    @Test
    public void testGetDCharEncapsulated() {

        DChar ch = DCharFactory.getDChar('A', false);
        assertEquals("Should return 'A'", ch.getEncapsulatedChar(), 'A');

    }
    @Test
    public void testGetDChars() {

        DChar[] ch = DCharFactory.getDChars("a");
        assertEquals("Should return 'A'", ch[0].getEncapsulatedChar(), 'A');

    }

}