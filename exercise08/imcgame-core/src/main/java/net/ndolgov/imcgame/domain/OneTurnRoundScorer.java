package net.ndolgov.imcgame.domain;

import java.util.Iterator;
import java.util.List;
import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Scorer;
import net.ndolgov.imcgame.util.Util;

/**
 * The Scorer to use in a game of 2-move rounds
 * @param <T> the type of the game items
 */
public final class OneTurnRoundScorer<T> implements Scorer<T> {
    private final ItemComparator<T> comparator;

    public OneTurnRoundScorer(ItemComparator<T> comparator) {
        this.comparator = comparator;
    }

    public Player<T> score(Iterator<Move<T>> moves) {
        final List<Move<T>> toScore = Util.asList(moves);

        if (toScore.size() != Rounds.MOVES_PER_ROUND) {
            throw new IllegalArgumentException("Invalid number of moves to score: " + toScore.size());
        } else {
            return scoreOneTurnRound(toScore.get(0), toScore.get(1));
        }
    }

    private Player<T> scoreOneTurnRound(Move<T> byFirst, Move<T> bySecond) {
        if (byFirst.getPlayer() == bySecond.getPlayer()) {
            throw new IllegalArgumentException("Both moves are by the same player: " + byFirst.getPlayer());
        } else {
            return this.comparator
                .compareTo(byFirst.getItem(), bySecond.getItem())
                .choose(byFirst.getPlayer(), Nobody.singleton(), bySecond.getPlayer());
        }
    }
}
