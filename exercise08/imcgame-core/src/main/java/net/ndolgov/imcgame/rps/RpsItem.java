package net.ndolgov.imcgame.rps;

/**
 * The items revealed in the RPS game
 */
public enum RpsItem {
    ROCK {
        boolean beats(RpsItem another) {
            return another == SCISSORS;
        }
    },
    PAPER {
        boolean beats(RpsItem another) {
            return another == ROCK;
        }
    },
    SCISSORS {
        boolean beats(RpsItem another) {
            return another == PAPER;
        }
    };

    /**
     * @return true if this item beats another one
     */
    abstract boolean beats(RpsItem another);
}