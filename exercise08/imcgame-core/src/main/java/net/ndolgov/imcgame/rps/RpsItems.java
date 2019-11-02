package net.ndolgov.imcgame.rps;

import java.util.function.Function;
import net.ndolgov.imcgame.Scorer.ItemComparator;

public final class RpsItems {
    /**
     * @return comparator of the RPS game items
     */
    public static ItemComparator<RpsItem> comparator() {
        return new RpsItemComparator();
    }

    /**
     * @return mapping from integers (e.g. random numbers from limited range) to the RPS game items
     */
    public static Function<Integer, RpsItem> byIndexFactory() {
        return (index) -> {
            switch(index) {
                case 0:
                    return RpsItem.ROCK;
                case 1:
                    return RpsItem.PAPER;
                case 2:
                    return RpsItem.SCISSORS;
                default:
                    throw new IllegalArgumentException("Invalid item index: " + index);
            }
        };
    }

    private static final class RpsItemComparator implements ItemComparator<RpsItem> {
        public ItemComparison compareTo(RpsItem left, RpsItem right) {
            if (left == right) {
                return ItemComparison.EQ;
            } else {
                return left.beats(right) ? ItemComparison.GT : ItemComparison.LT;
            }
        }

        private RpsItemComparator() {
        }
    }

    private RpsItems() {
    }
}