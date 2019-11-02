package net.ndolgov.imcgame;

/**
 * A move in the game made by a player to reveal an item
 * @param <T> the type of the game item
 */
public interface Move<T> {
    T getItem();

    Player<T> getPlayer();
}