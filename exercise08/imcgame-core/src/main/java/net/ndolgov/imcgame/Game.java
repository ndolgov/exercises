package net.ndolgov.imcgame;

/**
 * A game accepts and remembers moves
 * @param <T> the type of the game item
 */
public interface Game<T> {
    /**
     * @return true when the requested number of rounds is reached
     */
    boolean isOver();

    /**
     * Accept the next move in this game as long as it's made by one of the known players.
     */
    void onNextMove(Move<T> move);

    /**
     * @return the rounds finished so far in this game
     */
    Iterable<Round<T>> history();
}