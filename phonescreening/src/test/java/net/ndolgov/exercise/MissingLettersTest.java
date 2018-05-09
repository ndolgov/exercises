package net.ndolgov.exercise;

import org.junit.Test;

import static org.junit.Assert.*;

public class MissingLettersTest {
    @Test
    public void test() {
        assertEquals("", MissingLetters.getMissingLetters("A quick brown fox jumps over the lazy dog"));
        assertEquals("bjkmqz", MissingLetters.getMissingLetters("A slow yellow fox crawls under the proactive dog"));
        assertEquals("cfjkpquvwxz", MissingLetters.getMissingLetters("Lions, and tigers, and bears, oh my!"));
        assertEquals("abcdefghijklmnopqrstuvwxyz", MissingLetters.getMissingLetters(""));
    }
}
