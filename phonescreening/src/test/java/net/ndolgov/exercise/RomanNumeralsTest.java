package net.ndolgov.exercise;

public class RomanNumeralsTest {
    public static void main(String[] args) {
    /*
      I 1
      V 5
      X 10
      L 50
      C 100
      http://romannumerals.babuo.com/CCCXCIII-roman-numerals
    */
        System.out.println(toRoman(10));

        System.out.println(toRoman(3));
        System.out.println(toRoman(22));
        System.out.println(toRoman(4));
        System.out.println(toRoman(9));
        System.out.println(toRoman(39));
        System.out.println(toRoman(55));
        System.out.println(toRoman(59));
        System.out.println(toRoman(88));
        System.out.println(toRoman(155));
        System.out.println(toRoman(5555));
    }

    private static final int[] by = {10, 50, 100, 500, 1_000};
    private static final String[] by_digits = {"X", "L", "C", "D", "M"};
    private static final String[] digits = {"", "I", "II","III","IV","V","VI","VII","VIII","IX"};

    private static String toRoman(int value) {
        String sb = "";
        int d = 4;
        long v = value;

        while (v > 0) {
            if (d < 0) {
                return sb + digits[(int) v];
            }

            final int next = (int) (v / by[d]);
            if (next > 0) {
                v = v - (by[d] * next);

                if (d == -1) {
                    sb = sb + digits[next];
                } else {
                    for (int i = 0; i< next; i++) {
                        sb = sb + by_digits[d];
                    }
                }
            }

            d--;
        }

        return sb;
    }
}

