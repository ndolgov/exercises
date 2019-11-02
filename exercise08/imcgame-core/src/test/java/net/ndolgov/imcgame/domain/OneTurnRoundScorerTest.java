package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.rps.RpsItem;
import net.ndolgov.imcgame.rps.RpsItems;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class OneTurnRoundScorerTest {
    @Test
    public void testWinnerSelection() {
        final Player player1 = mock(Player.class);
        final Player player2 = mock(Player.class);

        final OneTurnRoundScorer<RpsItem> scorer = new OneTurnRoundScorer<>(RpsItems.comparator());

        final List<Move<RpsItem>> winnerLoserMoves = new ArrayList<>();
        winnerLoserMoves.add(new PlayerMove<>(player1, RpsItem.ROCK));
        winnerLoserMoves.add(new PlayerMove<>(player2, RpsItem.PAPER));
        assertEquals(player2, scorer.score(winnerLoserMoves.iterator()));

        final List<Move<RpsItem>> loserWinnerMoves = new ArrayList<>();
        loserWinnerMoves.add(new PlayerMove<>(player1, RpsItem.SCISSORS));
        loserWinnerMoves.add(new PlayerMove<>(player2, RpsItem.PAPER));
        assertEquals(player1, scorer.score(loserWinnerMoves.iterator()));
    }

    @Test
    public void testNobodyWinsADraw() {
        final List<Move<RpsItem>> moves = new ArrayList<>();
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.ROCK));
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.ROCK));
        assertEquals(Nobody.singleton(), new OneTurnRoundScorer<>(RpsItems.comparator()).score(moves.iterator()));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testConsecutiveMovesByTheSamePlayerAreRejected() {
        final List<Move<RpsItem>> moves = new ArrayList<>();
        final Player player = mock(Player.class);
        moves.add(new PlayerMove<>(player, RpsItem.ROCK));
        moves.add(new PlayerMove<>(player, RpsItem.PAPER));
        new OneTurnRoundScorer<>(RpsItems.comparator()).score(moves.iterator());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testTooFewMovesAreRejected() {
        final List<Move<RpsItem>> moves = new ArrayList<>();
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.ROCK));
        new OneTurnRoundScorer<>(RpsItems.comparator()).score(moves.iterator());
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testTooManyMovesAreRejected() {
        final List<Move<RpsItem>> moves = new ArrayList<>();
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.ROCK));
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.PAPER));
        moves.add(new PlayerMove<>(mock(Player.class), RpsItem.SCISSORS));
        new OneTurnRoundScorer<>(RpsItems.comparator()).score(moves.iterator());
    }
}