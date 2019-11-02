package net.ndolgov.imcgame;

import java.util.Iterator;

/**
 * A way to collect and report game statistics
 * @param <T> the type of the game item
 */
public interface GameStatsCollector<T> {
    /**
     * @param game a game with the history of moves preserved
     * @return player statistics extracted from a given game's history
     */
    Iterator<PlayerStats> stats(Game<T> game);

    /**
     * Player statistics to base reports on
     */
    final class PlayerStats {
        public final String playerId;
        public final int won;
        public final int lost;

        public PlayerStats(String playerId, int won, int lost) {
            this.playerId = playerId;
            this.won = won;
            this.lost = lost;
        }

        public String toString() {
            return "{" + this.playerId + ":won=" + this.won + ", lost=" + this.lost + "}";
        }
    }
}