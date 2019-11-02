package net.ndolgov.imcgame.domain;

import net.ndolgov.imcgame.Move;
import net.ndolgov.imcgame.Player;

public final class PlayerMove<T> implements Move<T> {
    private final Player<T> player;

    private final T item;

    public PlayerMove(Player<T> player, T item) {
        this.player = player;
        this.item = item;
    }

    public T getItem() {
        return this.item;
    }

    public Player<T> getPlayer() {
        return this.player;
    }

    public String toString() {
        return "{" + this.player + " : " + this.item + "}";
    }
}