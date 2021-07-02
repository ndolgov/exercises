package net.ndolgov.exercise2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CountPlaneSeating {
    private static final Map<Character, Integer> charToIndex = new HashMap<>();
    static {
        charToIndex.put('A', 0);
        charToIndex.put('B', 1);
        charToIndex.put('C', 2);
        charToIndex.put('D', 3);
        charToIndex.put('E', 4);
        charToIndex.put('F', 5);
        charToIndex.put('G', 6);
        charToIndex.put('H', 7);
        charToIndex.put('J', 8);
        charToIndex.put('K', 9);
    }

    public int solution(int N, String S) {
        if (N <= 0) {
            return 0;
        }
        if (S == null || S.isEmpty()) {
            return N * 2;
        }

        final boolean[][] seats = new boolean[N][10];
        for (int i = 0; i < N; i++) {
            Arrays.fill(seats[i], false);
        }

        for (String seat : S.split(" ")) {
            seats[Integer.parseInt(seat.substring(0, seat.length()-1)) - 1][charToIndex.get(seat.charAt(seat.length()-1))] = true;
        }

        int maxFamilyCount = 0;
        for (int i = 0; i < N; i++) {
            maxFamilyCount += findMaxFamilyCount(seats[i]);
        }

        return maxFamilyCount;
    }

    private static int findMaxFamilyCount(boolean[] seat) {
        final boolean beVacant = isSeatRangeVacant(seat, charToIndex.get('B'), charToIndex.get('E'));
        final boolean fjVacant = isSeatRangeVacant(seat, charToIndex.get('F'), charToIndex.get('J'));
        final boolean dgVacant = isSeatRangeVacant(seat, charToIndex.get('D'), charToIndex.get('G'));

        if (beVacant && fjVacant) {
            return 2;
        }

        return beVacant || fjVacant || dgVacant ? 1 : 0;
    }

    private static boolean isSeatRangeVacant(boolean[] seat, int from, int to) {
        for (int j = from; j <= to; j++) {
            if (seat[j]) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        //System.out.println(new Main().solution2(123456));

        //System.out.println(new Main().solution2(1234567));

        //        for (int i = 0; i < N; i++) {
//            System.out.println(Arrays.toString(seats[i])); //todo
//        }
        //System.out.println(maxFamilyCount + " / " + Arrays.toString(seats[i])); //todo
        System.out.println(new CountPlaneSeating().solution(2, "1A 2F 1C"));
    }

    public int solution1(int A) {
        final String strA = String.valueOf(A);
        final StringBuilder strResult = new StringBuilder(16);

        int l = 0;
        int r = strA.length() - 1;
        while (l < r) {
            strResult.append(strA.charAt(l++)).append(strA.charAt(r--));
        }

        if (l == r) { // odd number of digits
            strResult.append(strA.charAt(l));
        }

        return Integer.parseInt(strResult.toString());
    }
}
