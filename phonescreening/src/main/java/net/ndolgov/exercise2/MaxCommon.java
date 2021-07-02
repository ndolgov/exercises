package net.ndolgov.exercise2;

import java.util.Arrays;

class MaxCommon {
    public static void main(String[] args) {
        //System.out.println(maxCommonString("abeabcf", "abcd"));
        //System.out.println(maxCommonString2("abeabcdf", "abcde"));

        final String str1 = "abeabcf";
        final String str2 = "abcde";
        System.out.println(LCSubStr(str1.toCharArray(), str2.toCharArray(), str1.length(), str2.length()));

        System.out.println(maxCommonString2(str1, str2));
    }

    public static int maxCommonString(String a, String b) {
        return maxCommon(a, b, 0, 0);

    }

    private static int maxCommon(String a, String b, int aptr, int bptr) {
        if (aptr == a.length() || bptr == b.length()) {
            return 0;
        }

        if (a.charAt(aptr) == b.charAt(bptr)) {
            return maxCommon(a, b, aptr + 1, bptr + 1) + 1;
        }

        final int maxa = maxCommon(a, b, aptr + 1, bptr);
        final int maxb = maxCommon(a, b, aptr, bptr + 1);

        return Math.max(maxa, maxb);

    }

    public static int maxCommonString2(String a, String b) {
        final int[][] m = new int[a.length()][b.length()];

        for (int i = 0; i < a.length(); i++) {
            m[i][0] = a.charAt(i) == b.charAt(0) ? 1 : 0;
        }

        for (int j = 0; j < b.length(); j++) {
            m[0][j] = a.charAt(0) == b.charAt(j) ? 1 : 0;
        }

        for (int i = 1; i < a.length(); i++) {
            System.out.println(Arrays.toString(m[i]));
        }

        int maxCommonLength = 0;
        for (int i = 1; i < a.length(); i++) {
            for (int j = 1; j < b.length(); j++) {
                final int maxSoFar = Math.max(m[i - 1][j], m[i][j - 1]);
                if (a.charAt(i) == b.charAt(j)) {
                    m[i][j] = maxSoFar + 1;
                    maxCommonLength = Math.max(maxCommonLength, m[i][j]);
                } else {
                    m[i][j] = 0;//maxSoFar;
                }
//                m[i][j] = a.charAt(i) == b.charAt(j) ? maxSoFar + 1 : maxSoFar;
//                maxCommonLength = Math.max(maxCommonLength, m[i][j]);

//                if (i == j) {
//                    m[i][j] = a.charAt(i) == b.charAt(j) ? maxSoFar + 1 : 0;
//                } else {
//                    m[i][j] = a.charAt(i) == b.charAt(j) ? maxSoFar + 1 : maxSoFar;
//                }
            }
        }


        for (int i = 1; i < a.length(); i++) {
            System.out.println(Arrays.toString(m[i]));
        }

        //return m[a.length() - 1][b.length() - 1];
        return maxCommonLength;
    }

    static int LCSubStr(char X[], char Y[], int m, int n)
    {
        // Create a table to store lengths of longest common suffixes of
        // substrings. Note that LCSuff[i][j] contains length of longest
        // common suffix of X[0..i-1] and Y[0..j-1]. The first row and
        // first column entries have no logical meaning, they are used only
        // for simplicity of program
        int LCStuff[][] = new int[m + 1][n + 1];
        int result = 0;  // To store length of the longest common substring

        // Following steps build LCSuff[m+1][n+1] in bottom up fashion
        for (int i = 0; i <= m; i++)
        {
            for (int j = 0; j <= n; j++)
            {
                if (i == 0 || j == 0)
                    LCStuff[i][j] = 0;
                else if (X[i - 1] == Y[j - 1])
                {
                    LCStuff[i][j] = LCStuff[i - 1][j - 1] + 1;
                    result = Integer.max(result, LCStuff[i][j]);
                }
                else
                    LCStuff[i][j] = 0;
            }
        }

        for (int i = 1; i < LCStuff.length; i++) {
            System.out.println(Arrays.toString(LCStuff[i]));
        }

        return result;
    }

}

