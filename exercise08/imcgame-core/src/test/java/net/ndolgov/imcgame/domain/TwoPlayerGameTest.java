package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.GameStatsCollector.PlayerStats;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Strategy;
import net.ndolgov.imcgame.rps.RpsGames;
import net.ndolgov.imcgame.rps.RpsItem;
import net.ndolgov.imcgame.rps.RpsItems;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class TwoPlayerGameTest {
    @Test
    public void testGameplay() {
        final Strategy<RpsItem> firstStrategy = mock(Strategy.class);
        when(firstStrategy.nextMove(anyCollection())).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.ROCK);

        final Strategy<RpsItem> secondStrategy = mock(Strategy.class);
        when(secondStrategy.nextMove(anyCollection())).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.PAPER).
            thenReturn(RpsItem.ROCK).
            thenReturn(RpsItem.PAPER).
            thenReturn(RpsItem.SCISSORS);

        final Player<RpsItem> first = new GamePlayer<>("P1", firstStrategy);
        final Player<RpsItem> second = new GamePlayer<>("P2", secondStrategy);

        final int nRounds = 5;
        final TwoPlayerGame<RpsItem> game = new TwoPlayerGame<>(
                "RPSGame",
                nRounds,
                Rounds::createNewRound,
                first,
                second,
                new OneTurnRoundScorer<>(RpsItems.comparator()));

        for (int i = 0; i < nRounds; i++) {
            assertFalse(game.isOver());
            game.onNextMove(first.nextMove(game.history()));
            game.onNextMove(second.nextMove(game.history()));
        }
        assertTrue(game.isOver());

        final Iterator<PlayerStats> stats = (new PlayerGameStatsCollector<RpsItem>()).stats(game);
        stats.forEachRemaining(playerStats -> {
            if (playerStats.playerId.equals(first.getId())) {
                assertEquals(1, playerStats.won);
                assertEquals(2, playerStats.lost);
            } else if (playerStats.playerId.equals(second.getId())) {
                assertEquals(2, playerStats.won);
                assertEquals(1, playerStats.lost);
            } else {
                fail("Draws are expected to be filtered out");
            }
        });
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testIllegalPlayerIsRejected() {
        final TwoPlayerGame<RpsItem> game = RpsGames.createNewGameOfTwoComputers(3);
        game.onNextMove(new PlayerMove<>(mock(Player.class), RpsItem.SCISSORS));
    }
}