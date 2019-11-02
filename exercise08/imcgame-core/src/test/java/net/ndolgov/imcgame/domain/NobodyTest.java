package net.ndolgov.imcgame.domain;

import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;

public class NobodyTest {
    @Test
    public void testIsSingleton() {
        assertEquals(Nobody.singleton(), Nobody.singleton());
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testCanNotPlay() {
        Nobody.singleton().nextMove(Collections.emptyList());
    }
}