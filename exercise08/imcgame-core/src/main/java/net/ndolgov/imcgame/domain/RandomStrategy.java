package net.ndolgov.imcgame.domain;

import java.util.Random;
import java.util.function.Function;
import net.ndolgov.imcgame.Round;
import net.ndolgov.imcgame.Strategy;

public final class RandomStrategy<T> implements Strategy<T> {
    private final int itemCardinality;

    private final Function<Integer, T> factory;

    private final Random random = new Random();

    public RandomStrategy(int itemCardinality, Function<Integer, T> factory) {
        this.itemCardinality = itemCardinality;
        this.factory = factory;
    }

    public T nextMove(Iterable<Round<T>> history) {
        int index = random.nextInt(itemCardinality);
        return factory.apply(index);
    }
}