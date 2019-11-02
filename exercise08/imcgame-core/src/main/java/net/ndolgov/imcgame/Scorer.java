package net.ndolgov.imcgame;

import java.util.Iterator;

/**
 * A means of deciding on a winner after a series of moves
 * @param <T> the type of the game item
 */
public interface Scorer<T> {
    /**
     * @param moves the moves in a round to score
     * @return the winner of the round or {@link net.ndolgov.imcgame.domain.Nobody} in the case of a draw
     */
    Player<T> score(Iterator<Move<T>> moves);

    interface ItemComparator<T> {
        /**
         * @param first the first item
         * @param second the second item
         * @return an abstraction that represents the result of comparison between two items
         */
        ItemComparison compareTo(T first, T second);

        enum ItemComparison {
            GT {
                public <T> T choose(T ifGt, T ifEq, T ifLt) {
                    return ifGt;
                }
            },
            EQ {
                public <T> T choose(T ifGt, T ifEq, T ifLt) {
                    return ifEq;
                }
            },
            LT {
                public <T> T choose(T ifGt, T ifEq, T ifLt) {
                    return ifLt;
                }
            };

            /**
             * @param ifGt the object to return if the first item beats the second one
             * @param ifEq the object to return if the first item is identical to the second one
             * @param ifLt the object to return if the second item beats the first one
             * @param <T> the type of the compared items
             * @return one of the three alternatives
             */
            public abstract <T> T choose(T ifGt, T ifEq, T ifLt);
        }
    }
}