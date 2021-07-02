package net.ndolgov.exercise2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class ZipR {
    public static void main(String[] args) {
        //zip(0, 100);

        final ArrayList<Integer> start = new ArrayList<>();
        start.add(10);
        start.add(5);
        start.add(6);
        start.add(3);
        start.add(2);
        start.add(4);
        start.add(1);
        start.add(7);
        start.add(9);
        start.add(8);
        start.add(0);
        System.out.println(thirdBiggest(start));
        start.sort(Integer::compare);
        System.out.println(start);
    }

    static void zip(int first, int last) {
        if (first < 0 || last < 0 || first > last) {
            return;
        }

        for (int i = first; i <= last; i++) {
            if (isMultiple(i, 3) && isMultiple(i, 5)) {
                System.out.println("ZipRecruiter");
            } else if (isMultiple(i, 7)) {
                System.out.println("Zip");
            } else if (isMultiple(i, 13)) {
                System.out.println("Recruiter");
            } else {
                System.out.println(i);
            }
        }
    }

    private static boolean isMultiple(int x, int n) {
        return x % n == 0;
    }

    // PQ(3) -> additional O(N), O(N*lgN) complexity//int a = Integer.MIN_VALUE, b = Integer.MIN_VALUE, c = Integer.MIN_VALUE;
    static int thirdBiggest(List<Integer> numbers) {

        if (numbers.size() < 3) {
            throw new IllegalArgumentException("Not enough numbers");
        }

        final PriorityQueue<Integer> pq = new PriorityQueue<>(4);//, (l, r) -> Integer.compare(r, l));

        for (int i : numbers) {
            pq.add(i);

            if (pq.size() > 3) {
                pq.poll();
            }
        }

        return pq.poll();
    }

    static boolean reOrg(String string1, String string2) {
        if (string1 == null || string2 == null) {
            return false;
        }
        if (string1.isEmpty() && string2.isEmpty()) {
            return true;
        }
        if (string1.length() != string2.length()) {
            return false;
        }

        final HashMap<Character, Integer> chToCount = new HashMap<>();
        for (int i = 0; i < string1.length(); i++) {
            chToCount.compute(string1.charAt(i), (ch, count) -> (count == null) ? 1 : count + 1);
        }

        for (int i = 0; i < string2.length(); i++) {
            final char letter = string2.charAt(i);

            final Integer count = chToCount.get(letter);
            if (count == null) {
                return false;
            }

            final int newCount = count - 1;
            if (count < 0) {
                return false;
            } else {
                chToCount.put(letter, newCount);
            }
        }

        return true;
    }
}
