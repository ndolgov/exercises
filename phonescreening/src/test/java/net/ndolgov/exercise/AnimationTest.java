package net.ndolgov.exercise;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnimationTest {
    @Test
    public void testAllDots() {
        assertArrayEquals(new String[]{"......."}, Animation.animate(2, "......."));
    }

    @Test
    public void testOneR() {
        final String[] expected =
            {"..X....",
             "....X..",
             "......X",
             "......."};

        assertArrayEquals(expected, Animation.animate(2, "..R...."));
    }

    @Test
    public void testRRLRL() {
        final String[] expected =
            { "XX..XXX",
               ".X.XX..",
               "X.....X",
               "......."};

        assertArrayEquals(expected, Animation.animate(3, "RR..LRL"));
    }
}
