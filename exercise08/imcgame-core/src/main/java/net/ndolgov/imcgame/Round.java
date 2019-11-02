package net.ndolgov.imcgame;

import java.util.Iterator;

/**
 * A round of a game that results in a winner after a few moves.
 * @param <T> the type of the game item
 */
public interface Round<T> {
    /**
     * @return true if this round will not accept more moves
     */
    boolean isFinished();

    /**
     * @return the winner of this round if the round has been scored
     */
    Player<T> getWinner();

    /**
     * @return the moves in this round so far
     */
    Iterator<Move<T>> getMoves();

    /**
     * @param move the next move to make in this round
     * @return round state updated with the given move
     */
    Round<T> onNextMove(Move<T> move);

    /**
     * @param scorer the scorer to use to decide on the winner
     * @return the final state of this round ready to be preserved for posterity
     */
    Round<T> score(Scorer<T> scorer);
}
