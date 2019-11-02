package net.ndolgov.imcgame;

/**
 * A strategy makes possible non-random choice of moves by non-humanoid players.
 * The recent history of moves is accumulated and shared with strategies to enable ML-based optimization.
 *
 * @param <T> the type of the game item
 */
public interface Strategy<T> {
    /**
     * @param history the rounds finished by now in the current game
     * @return the next item to reveal according to this strategy
     */
    T nextMove(Iterable<Round<T>> history);
}

