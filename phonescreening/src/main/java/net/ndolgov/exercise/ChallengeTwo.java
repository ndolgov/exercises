package net.ndolgov.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Assume a single whitespace between words
 */
public final class ChallengeTwo {
    private static final String STR = "Hello Mike, This is a long sentence from Quad Analytix. It needs to be split into small segments of given size so that no word is split up. Enjoy writing this program.";

    public static void main(String[] args) {
        try {
            for (String substring : new ChallengeTwo().splitMessage(STR, 46)) {
                System.out.println(substring + " : " + substring.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> splitMessage(String inputString, int maxLength) {
        if ((inputString == null) || (inputString.length() == 0)) {
            return Collections.emptyList();
        }

        if (inputString.length() <= maxLength) {
            final List<String> substrings = new ArrayList<>();
            substrings.add(inputString);
            return substrings;
        }

        // todo check for unreasonable maxLength

        return new Splitter(inputString, maxLength).split();
    }

    private static final class Splitter {
        private final String inputString;
        private final int maxLength;
        private final List<String> substrings = new ArrayList<>();

        public Splitter(String inputString, int maxLength) {
            this.inputString = inputString;
            this.maxLength = maxLength;
        }

        public List<String> split() {
            doSplit(0, nextWhiteSpaceIndex(0));
            return substrings;
        }

        /**
         *
         * @param left left index of the next substring in the input string
         * @param right right index of the next substring in the input string
         */
        private void doSplit(int left, int right) {
            if ((right - left) > maxLength) {
                throw new IllegalArgumentException("A single word is too long: [" + left + ":" + right + "]");
            }

            if (right == inputString.length()) {
                substrings.add(inputString.substring(left, right));
                return;
            }

            if ((right - left) == maxLength) {
                substrings.add(inputString.substring(left, right));
                doSplit(right + 1, nextWhiteSpaceIndex(right));
            }

            final int nextR = nextWhiteSpaceIndex(right);
            if ((nextR - left) > maxLength) {
                substrings.add(inputString.substring(left, right));
                doSplit(right + 1, nextR);
            } else {
                doSplit(left, nextR);
            }
        }

        private int nextWhiteSpaceIndex(int lastWhiteSpaceIndex) {
            for (int i = lastWhiteSpaceIndex + 1; i < inputString.length(); i++) {
                if (inputString.charAt(i) == ' ') {
                    return i;
                }
            }
            return inputString.length();
        }
    }
}
