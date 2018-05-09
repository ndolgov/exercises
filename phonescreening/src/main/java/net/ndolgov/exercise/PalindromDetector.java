package net.ndolgov.exercise;

import java.io.*;
import java.util.*;
import javafx.util.Pair;

class PalindromDetector {
    public static void main(String[] args) {
        //final String str = "abba";
        final String str = "abbacdefg";
        System.out.println(str);
        System.out.println(pali2(str));
        detect("abba");
        detect("cabba");
        detect("abbacdefg");
        //detect("somewordlongpalindromemordnilapgnolsomemwords");
        //detect("okaylongpalindromeemordnilapgnolsomemorewords");
    }

    private static void detect(String input) {
        System.out.println(input + " -> " + pali2(input));
    }

    private static String pali2(String input) {
        final char[] str = input.toCharArray();

        int l = 0;
        int r = str.length - 1;

        Pair<Integer, Integer> p = isPali2(str, l, r, l, r);
        return isValid(p) ? input.substring(p.getKey(), p.getValue() + 1) : input.substring(0, 1);
    }

    private static Pair<Integer, Integer> isPali2(char[] str, int l, int r, int lp, int rp) {
        if (l >= r) {
            return new Pair<>(lp, rp);
        }
        //System.out.println(new String(str, l, r - l + 1));

        if (str[l] == str[r]) {
            return isPali2(str, l + 1, r - 1, lp, rp);
        } else {
            Pair<Integer, Integer> p1 = isPali2(str, l + 1, r, l + 1, rp);
            Pair<Integer, Integer> p2 = isPali2(str, l, r - 1, lp, r - 1);

            if (isValid(p1) && isValid(p2)) {
                return max(p1, p2);
            }

            if (isValid(p1)) {
                return p1;
            }

            if (isValid(p2)) {
                return p2;
            }

            return new Pair<>(-1, -1);
        }
    }

    private static boolean isValid(Pair<Integer, Integer> p) {
        return (p.getKey() != -1) && (p.getValue() != -1);
    }

    private static Pair<Integer, Integer> max(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        return length(p1) >= length(p2) ? p1 : p2;
    }

    private static int length(Pair<Integer, Integer> p) {
        return p.getValue() - p.getKey() + 1;
    }
}
