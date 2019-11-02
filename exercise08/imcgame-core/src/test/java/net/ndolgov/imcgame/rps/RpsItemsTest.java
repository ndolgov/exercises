package net.ndolgov.imcgame.rps;

import net.ndolgov.imcgame.Scorer.ItemComparator;
import org.testng.annotations.Test;

import java.util.function.Function;

import static net.ndolgov.imcgame.rps.RpsItem.PAPER;
import static net.ndolgov.imcgame.rps.RpsItem.ROCK;
import static net.ndolgov.imcgame.rps.RpsItem.SCISSORS;
import static org.testng.Assert.assertEquals;

public class RpsItemsTest {
    @Test
    public void testComparator() {
        final Object left = new Object();
        final Object middle = new Object();
        final Object right = new Object();

        final ItemComparator<RpsItem> comparator = RpsItems.comparator();

        assertEquals(middle, comparator.compareTo(ROCK, ROCK).choose(left, middle, right));
        assertEquals(left, comparator.compareTo(ROCK, SCISSORS).choose(left, middle, right));
        assertEquals(right, comparator.compareTo(ROCK, PAPER).choose(left, middle, right));

        assertEquals(left, comparator.compareTo(PAPER, ROCK).choose(left, middle, right));
        assertEquals(right, comparator.compareTo(PAPER, SCISSORS).choose(left, middle, right));
        assertEquals(middle, comparator.compareTo(PAPER, PAPER).choose(left, middle, right));

        assertEquals(right, comparator.compareTo(SCISSORS, ROCK).choose(left, middle, right));
        assertEquals(middle, comparator.compareTo(SCISSORS, SCISSORS).choose(left, middle, right));
        assertEquals(left, comparator.compareTo(SCISSORS, PAPER).choose(left, middle, right));
    }

    @Test
    public void testByIndexFactory() {
        final Function<Integer, RpsItem> factory = RpsItems.byIndexFactory();
        assertEquals(ROCK, factory.apply(0));
        assertEquals(PAPER, factory.apply(1));
        assertEquals(SCISSORS, factory.apply(2));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testInvalidLowerIndex() {
        assertEquals(ROCK, RpsItems.byIndexFactory().apply(-1));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testInvalidUpperIndex() {
        assertEquals(ROCK, RpsItems.byIndexFactory().apply(4));
    }
}