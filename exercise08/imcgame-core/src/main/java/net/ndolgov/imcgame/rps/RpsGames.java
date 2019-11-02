package net.ndolgov.imcgame.rps;

import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.domain.GamePlayer;
import net.ndolgov.imcgame.domain.OneTurnRoundScorer;
import net.ndolgov.imcgame.domain.RandomStrategy;
import net.ndolgov.imcgame.domain.Rounds;
import net.ndolgov.imcgame.domain.TwoPlayerGame;

public final class RpsGames {
    public static TwoPlayerGame<RpsItem> createNewGame(int nRounds, Player<RpsItem> player1, Player<RpsItem> player2) {
        return new TwoPlayerGame<>(
            "Game" + System.currentTimeMillis(),
             nRounds, Rounds::createNewRound,
             player1,
             player2,
             new OneTurnRoundScorer<>(RpsItems.comparator()));
    }

    public static RandomStrategy<RpsItem> createRandomStrategy() {
        return new RandomStrategy<>(3, RpsItems.byIndexFactory());
    }

    public static TwoPlayerGame<RpsItem> createNewGameOfTwoComputers(int nRounds) {
        long now = System.currentTimeMillis();
        return new TwoPlayerGame<>(
            "Game" + now,
            nRounds, Rounds::createNewRound,
            new GamePlayer<>("C1-" + now, createRandomStrategy()),
            new GamePlayer<>("C2-" + now, createRandomStrategy()),
            new OneTurnRoundScorer<>(RpsItems.comparator()));
    }

    private RpsGames() {
    }
}
