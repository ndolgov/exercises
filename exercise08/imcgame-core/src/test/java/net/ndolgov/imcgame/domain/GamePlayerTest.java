package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Strategy;
import net.ndolgov.imcgame.rps.RpsItem;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class GamePlayerTest {
    @Test
    public void testMakingAMove() {
        final Strategy<RpsItem> strategy = mock(Strategy.class);
        when(strategy.nextMove(anyCollection())).thenReturn(RpsItem.ROCK);

        final String id = "id";
        final GamePlayer<RpsItem> player = new GamePlayer<>(id, strategy);
        assertEquals(id, player.getId());

        final Move<RpsItem> move = player.nextMove(Collections.emptyList());
        assertEquals(RpsItem.ROCK, move.getItem());
        assertEquals(player, move.getPlayer());
    }
}