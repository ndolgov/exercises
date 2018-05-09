package net.ndolgov.v2;

/**
 * This index uses a small binary tree to zero in on a subset of the container data. The idea is to split the original set
 * into a few (e.g. 32 in case of 32K containers) intervals and quickly
 * todo once the tree is built, replace it with an array representation heap-style
 */
final class IntervalIndex {
    final IntervalNode root;

    private final long[] data;

    // for 32 intervals: powerOfTwo = 5
    public IntervalIndex(int left, int right, int powerOfTwo, long[] data) {
        this.data = data;
        IntervalNode top = new IntervalNode((short) left, (short) right);
        for (int i = 0; i < powerOfTwo; i++) {
            top = top.split();
        }
        root = top;
    }

    final class IntervalNode {
        final IntervalNode left;
        final IntervalNode right;
        final int lIndex;
        final int rIndex;

        public IntervalNode(IntervalNode left, IntervalNode right, int l, int r) {
            this.left = left;
            this.right = right;
            this.lIndex = l;
            this.rIndex = r;
        }

        public IntervalNode(int left, int right) {
            this.left = null;
            this.right = null;
            this.lIndex = left;
            this.rIndex = right;
        }

        private IntervalNode split() {
            if (isLeaf()) {
                return doSplit();
            } else {
                return new IntervalNode(left.split(), right.split(), lIndex, rIndex);
            }
        }

        private IntervalNode doSplit() {
            final int mIndex = ((rIndex - lIndex) / 2) + lIndex; // todo adjust for duplicated values case
            return new IntervalNode(new IntervalNode(lIndex, mIndex), new IntervalNode(mIndex + 1, rIndex), lIndex, rIndex);
        }

        public void print() {
            if (isLeaf()) {
                System.out.println("[" + lIndex + " , " + rIndex + "]");
            } else {
                left.print();
                right.print();
            }
        }

        private boolean isLeaf() {
            return left == null;
        }

        public IntervalNode search(long value) {
            if (isLeaf()) {
                return (data[lIndex] <= value && value <= data[rIndex]) ? this : null;
            } else {
                return value <= data[left.rIndex] ? left.search(value) : right.search(value);
            }
        }

        public int size() {
            if (isLeaf()) {
                return 1;
            } else {
                return 1 + left.size() + right.size();
            }
        }

        @Override
        public String toString() {
            return "{IntervalNode:l=" + lIndex + " ,r=" + rIndex + "}";
        }
    }
}
