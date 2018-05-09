package net.ndolgov.exercise;

import java.util.Set;
import java.util.TreeSet;

public class MissingLetters {
    private static final String ALL_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";

    public static String getMissingLetters(String input) {
        if ((input == null) || (input.length() == 0)) {
            return ALL_LOWER_CASE;
        }

        return toString(missingLetters(input.toLowerCase()));
    }

    private static Set<Character> missingLetters(String input) {
        final Set<Character> filtered = allLowerCase();

        for (int i = 0; i < input.length(); i++) {
             filtered.remove(input.charAt(i));
        }

        return filtered;
    }

    private static Set<Character> allLowerCase() {
        final Set<Character> set = new TreeSet<>();

        for (int i = 0; i < ALL_LOWER_CASE.length(); i++) {
             set.add(ALL_LOWER_CASE.charAt(i));
        }

        return set;
    }

    private static String toString(Set<Character> letters) {
        final StringBuilder sb = new StringBuilder(letters.size());

        for (Character ch : letters) {
            sb.append(ch);
        }

        return sb.toString();
    }
}
