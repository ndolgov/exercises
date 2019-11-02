package net.ndolgov.imcgame;

/**
 * Someone who has a name and can make moves in the game.
 * @param <T> the type of the game item
 */
public interface Player<T> {
    String getId();

    /**
     * @param history the rounds finished by now in the current game
     * @return the next move by this player
     */
    Move<T> nextMove(Iterable<Round<T>> history);
}

