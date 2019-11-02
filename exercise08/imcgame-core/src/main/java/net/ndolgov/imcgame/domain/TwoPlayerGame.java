package net.ndolgov.imcgame.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ndolgov.imcgame.Game;
import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;
import net.ndolgov.imcgame.Scorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A game that allows two players to make moves until a requested number of rounds is finished.
 * @param <T> the type of the game items
 */
public final class TwoPlayerGame<T> implements Game<T> {
    private static final Logger log = LoggerFactory.getLogger(TwoPlayerGame.class);

    private final String gameId;

    private final int nRounds;

    private final List<Round<T>> finishedRounds = new ArrayList<>();

    private final Scorer<T> scorer;

    private final Player<T> first;

    private final Player<T> second;

    private final Supplier<Round<T>> createNewRound;

    private int iRound = 0;

    private Round<T> round;

    public TwoPlayerGame(String gameId, int nRounds, Supplier<Round<T>> createNewRound, Player<T> first, Player<T> second, Scorer<T> scorer) {
        this.gameId = gameId;
        this.nRounds = nRounds;
        this.createNewRound = createNewRound;
        this.first = first;
        this.second = second;
        this.scorer = scorer;
        this.setRound(this.createNewRound.get());
    }

    public boolean isOver() {
        return this.iRound >= this.nRounds;
    }

    public void onNextMove(Move<T> move) {
        if (move.getPlayer() != first && move.getPlayer() != second) {
            throw new IllegalArgumentException("The player is not in this game: " + move.getPlayer());
        }

        if (isOver()) {
            return;
        }

        setRound(this.round.onNextMove(move));

        if (round.isFinished()) {
            finishedRounds.add(this.round.score(this.scorer));
            setRound(createNewRound.get());
            iRound++;
        }
    }

    private void setRound(Round<T> round) {
        log.debug("Next round: " + round);
        this.round = round;
    }

    public Iterable<Round<T>> history() {
        return this.finishedRounds;
    }

    public String toString() {
        return "{TwoPlayerGame:" + this.gameId + "}";
    }
}