package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;

/**
 * The quasi-player reported as the winner of a round when real players reveal identical items.
 */
public final class Nobody implements Player<Object> {
    private static Nobody ourInstance = new Nobody();

    public static <T> Player<T> singleton() {
        return (Player<T>) ourInstance;
    }

    private Nobody() {
    }

    public String getId() {
        return "$NOBODY";
    }

    public Move<Object> nextMove(Iterable<Round<Object>> history) {
        throw new IllegalStateException("The round is finished");
    }

    public String toString() {
        return this.getId();
    }
}
