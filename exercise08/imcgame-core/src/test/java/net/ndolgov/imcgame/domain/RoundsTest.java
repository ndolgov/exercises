package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;
import net.ndolgov.imcgame.Round;
import net.ndolgov.imcgame.Scorer;
import net.ndolgov.imcgame.rps.RpsItem;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RoundsTest {
    @Test
    public void testInitialRound() {
        final Round<RpsItem> initial = Rounds.createNewRound();
        assertFalse(initial.getMoves().hasNext());
        assertFalse(initial.isFinished());
        initial.onNextMove(newMove());
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testInitialRoundHasNoWinner() {
        Rounds.createNewRound().getWinner();
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testInitialRoundCanNotBeScored() {
        Rounds.createNewRound().score(mock(Scorer.class));
    }

    @Test
    public void testInProgressRound() {
        final Move<RpsItem> firstMove = newMove();
        final Round<RpsItem> inProgress = Rounds.<RpsItem>createNewRound().onNextMove(firstMove);
        assertFalse(inProgress.isFinished());

        final Iterator oneMove = inProgress.getMoves();
        assertTrue(oneMove.hasNext());
        assertEquals(firstMove, oneMove.next());
        assertFalse(oneMove.hasNext());

        final Move<RpsItem> secondMove = newMove();
        final Round<RpsItem> finished = inProgress.onNextMove(secondMove);
        assertTrue(finished.isFinished());

        final Iterator twoMoves = finished.getMoves();
        assertTrue(twoMoves.hasNext());
        assertEquals(firstMove, twoMoves.next());
        assertTrue(twoMoves.hasNext());
        assertEquals(secondMove, twoMoves.next());
        assertFalse(twoMoves.hasNext());
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testInProgressRoundHasNoWinner() {
        Rounds.<RpsItem>createNewRound().onNextMove(newMove()).getWinner();
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testInProgressRoundCanNotBeScored() {
        Rounds.<RpsItem>createNewRound().onNextMove(newMove()).score(mock(Scorer.class));
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testConsecutiveMovesByTheSamePlayerAreRejected() {
        final Player<RpsItem> player = mock(Player.class);
        Rounds
            .<RpsItem>createNewRound()
            .onNextMove(new PlayerMove<>(player, RpsItem.ROCK))
            .onNextMove(new PlayerMove<>(player, RpsItem.ROCK));
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testTooManyMovesAreRejected() {
        final Player<RpsItem> player1 = mock(Player.class);
        final Player<RpsItem> player2 = mock(Player.class);
        Rounds
            .<RpsItem>createNewRound()
            .onNextMove(new PlayerMove<>(player1, RpsItem.ROCK))
            .onNextMove(new PlayerMove<>(player2, RpsItem.PAPER))
            .onNextMove(new PlayerMove<>(player1, RpsItem.SCISSORS));
    }

    @Test
    public void testScoredRound() {
        final Player winner = mock(Player.class);
        final Round<RpsItem> scored = newScoredRound(winner);

        assertTrue(scored.isFinished());
        assertEquals(winner, scored.getWinner());
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testScoredRoundCanMakeNoMoves() {
        newScoredRound(mock(Player.class)).onNextMove(newMove());
    }

    @Test (expectedExceptions = IllegalStateException.class)
    public void testScoredRoundCanNotBeScored() {
        newScoredRound(mock(Player.class)).score(mock(Scorer.class));
    }

    private static Move<RpsItem> newMove() {
        return new PlayerMove<RpsItem>(mock(Player.class), RpsItem.ROCK);
    }

    private static Round<RpsItem> newScoredRound(Player winner) {
        final Scorer<RpsItem> scorer = mock(Scorer.class);
        when(scorer.score(any())).thenReturn(winner);

        return Rounds
            .<RpsItem>createNewRound()
            .onNextMove(newMove())
            .onNextMove(newMove())
            .score(scorer);
    }
}