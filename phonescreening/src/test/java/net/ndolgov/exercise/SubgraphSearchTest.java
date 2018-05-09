package net.ndolgov.exercise;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SubgraphSearchTest {
    public static class Solution {
        private final int[][] m;
        private final boolean[][] isVisited;
        private final int w;
        private final int h;

        Solution(int[][] input) {
            m = input;
            w = m.length;
            h = m[0].length;

            isVisited = new boolean[w][h];
            for (int i = 0; i< w; i++) {
                for (int j = 0; j< h; j++) {
                    isVisited[i][j] = false;
                }
            }
        }

        public int count() {


            int count = 0;

            for (int i = 0; i< w; i++) {
                for (int j = 0; j< h; j++) {
                    count += visit(i, j);
                }
            }

            return count;
        }

        private int visit(int i, int j) {

            if (!isVisited[i][j]) {
                return doVisit(i,j) ? 1 : 0;
            }

            return 0;
        }

        private boolean doVisit(int i, int j) {
            if ((i<0) || (i>=w) || (j<0) || (j>=h)) {
                return false;
            }

            if (isVisited[i][j]) {
                return false;
            }

            isVisited[i][j] = true;

            if (m[i][j] == 1) {


                doVisit(i-1,j);
                doVisit(i+1,j);
                doVisit(i,j-1);
                doVisit(i,j+1);
                return true;
            }

            return false;
        }
    }

    @Test
    public void testLargeIsland() {
        assertEquals(1, new Solution(new int[][]{{1, 1, 0},{0,1,1},{0, 0 ,1}}).count());
    }

    @Test
    public void testSmallIslands() {
        assertEquals(3, new Solution(new int[][]{{1, 0, 0},{0,1,1},{1, 0 ,0}}).count());
    }
}
