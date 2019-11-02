package net.ndolgov.imcgame.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import net.ndolgov.imcgame.Game;
import net.ndolgov.imcgame.GameStatsCollector;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse game history to extract player statistics. Count the number of draws but exclude Nobody from the report.
 * @param <T> the type of the game item
 */
public final class PlayerGameStatsCollector<T> implements GameStatsCollector<T> {
    private static final Logger log = LoggerFactory.getLogger(GameStatsCollector.class);

    public Iterator<PlayerStats> stats(Game<T> game) {
        final Reducer<T> collector = new Reducer<>();

        game.history().iterator().forEachRemaining(collector);
        log.debug("Detected " + collector.nDraws + " draws in " + collector.nTotal + " rounds");

        int nDecisive = collector.nTotal - collector.nDraws;
        return collector.playerToStats.entrySet().stream()
            .map((e) -> new PlayerStats((e.getKey()).getId(), e.getValue(), nDecisive - e.getValue()))
            .iterator();
    }

    private static final class Reducer<T> implements Consumer<Round<T>> {
        final Map<Player<T>, Integer> playerToStats = new HashMap<>();
        int nTotal = 0;
        int nDraws = 0;

        public void accept(Round<T> round) {
            final Player<T> winner = round.getWinner();
            nTotal++;

            if (winner == Nobody.singleton()) {
                this.nDraws++; // count but exclude from the stats report
            } else {
                this.playerToStats.compute(winner, (k, v) -> v == null ? 1 : v + 1);
            }
        }
    }
}