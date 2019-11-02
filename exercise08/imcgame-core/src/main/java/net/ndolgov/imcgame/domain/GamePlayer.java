package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;
import net.ndolgov.imcgame.Strategy;

/**
 * A player delegating move decisions to a strategy
 */
public final class GamePlayer<T> implements Player<T> {
    private final Strategy<T> strategy;
    private final String id;

    public GamePlayer(String id, Strategy<T> strategy) {
        this.id = id;
        this.strategy = strategy;
    }

    public String getId() {
        return id;
    }

    public Move<T> nextMove(Iterable<Round<T>> history) {
        return new PlayerMove<>(this, strategy.nextMove(history));
    }

    public String toString() {
        return "{Player:" + id + "}";
    }
}