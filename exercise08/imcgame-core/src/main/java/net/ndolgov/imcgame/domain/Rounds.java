package net.ndolgov.imcgame.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;
import net.ndolgov.imcgame.Scorer;

/**
 * The states of a game round.
 */
public final class Rounds {
    public static int MOVES_PER_ROUND = 2; // TODO make configurable?

    /**
     * @return a newly created round in its initial state
     */
    public static <T> Round<T> createNewRound() {
        return new Rounds.NewRound<>();
    }

    /**
     * The round state before the first move is made
     */
    private static final class NewRound<T> implements Round<T> {
        private NewRound() {
        }

        public Round<T> onNextMove(Move<T> move) {
            return new RoundInProgress<>(move);
        }

        public boolean isFinished() {
            return false;
        }

        public Iterator<Move<T>> getMoves() {
            return Collections.emptyIterator();
        }

        public Player<T> getWinner() {
            throw new IllegalStateException("The round is not finished");
        }

        public Round<T> score(Scorer<T> scorer) {
            throw new IllegalStateException("The round is not finished");
        }

        public String toString() {
            return "{NewRound}";
        }
    }

    /**
     * The round state once at least one move has been made but before the round is scored
     */
    private static final class RoundInProgress<T> implements Round<T> {
        private final List<Move<T>> movesSoFar;

        public RoundInProgress(Move<T> move) {
            this(move, Collections.emptyList());
        }

        public RoundInProgress(Move<T> move, Collection<Move<T>> history) {
            this.movesSoFar = new ArrayList<>(history);
            this.movesSoFar.add(move);
        }

        public Round<T> onNextMove(Move<T> move) {
            if (this.isFinished()) {
                throw new IllegalStateException("The round is finished");
            }

            final Move<T> previousMove = movesSoFar.get(this.movesSoFar.size() - 1);
            if (previousMove.getPlayer() == move.getPlayer()) {
                throw new IllegalArgumentException("Wrong turn for player " + move.getPlayer());
            }

            return new RoundInProgress<>(move, movesSoFar);
        }

        public boolean isFinished() {
            return this.movesSoFar.size() >= Rounds.MOVES_PER_ROUND;
        }

        public Iterator<Move<T>> getMoves() {
            return this.movesSoFar.iterator();
        }

        public Player<T> getWinner() {
            throw new IllegalStateException("The round is not scored");
        }

        public Round<T> score(Scorer<T> scorer) {
            if (!this.isFinished()) {
                throw new IllegalStateException("The round is not finished");
            } else {
                return new ScoredRound<>(scorer.score(getMoves()), movesSoFar);
            }
        }

        public String toString() {
            return "{RoundInProgress:" + Arrays.toString(this.movesSoFar.toArray()) + "}";
        }
    }

    /**
     * The round state once the round is finished and scored
     */
    private static final class ScoredRound<T> implements Round<T> {
        private final Player<T> winner;

        private final Collection<Move<T>> moves;

        private ScoredRound(Player<T> winner, Collection<Move<T>> moves) {
            this.winner = winner;
            this.moves = moves;
        }

        public boolean isFinished() {
            return true;
        }

        public Player<T> getWinner() {
            return this.winner;
        }

        public Iterator<Move<T>> getMoves() {
            return this.moves.iterator();
        }

        public Round<T> onNextMove(Move<T> move) {
            throw new IllegalStateException("The round is finished");
        }

        public Round<T> score(Scorer<T> scorer) {
            throw new IllegalStateException("The round is finished");
        }

        public String toString() {
            return "{ScoredRound:" + winner + " : " + Arrays.toString(moves.toArray()) + "}";
        }
    }

    private Rounds() {
    }
}
