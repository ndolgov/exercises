package net.ndolgov.imcgame.rps;

import org.testng.annotations.Test;

import static net.ndolgov.imcgame.rps.RpsItem.ROCK;
import static net.ndolgov.imcgame.rps.RpsItem.PAPER;
import static net.ndolgov.imcgame.rps.RpsItem.SCISSORS;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RpsItemTest {
    @Test
    public void testBeatings() {
        assertFalse(ROCK.beats(ROCK));
        assertFalse(ROCK.beats(PAPER));
        assertTrue(ROCK.beats(SCISSORS));

        assertTrue(PAPER.beats(ROCK));
        assertFalse(PAPER.beats(PAPER));
        assertFalse(PAPER.beats(SCISSORS));

        assertFalse(SCISSORS.beats(ROCK));
        assertTrue(SCISSORS.beats(PAPER));
        assertFalse(SCISSORS.beats(SCISSORS));
    }
}