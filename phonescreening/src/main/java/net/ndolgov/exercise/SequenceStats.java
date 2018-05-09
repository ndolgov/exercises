package net.ndolgov.exercise;

import java.util.*;

// void push(int)   0 - 19000
// int mean()
// int median()

class SequenceStats {
    public static void main(String[] args) {
        SequenceStats s = new SequenceStats();
        s.push(10);
        s.push(30);
        s.push(20);


        System.out.println(s.mean() + " " + s.median());
    }

    private final Bucket[] buckets;
    private static final int BUCKET_SIZE = 1000;

    public SequenceStats() {
        buckets = new Bucket[20];
        for (int i = 0; i< buckets.length; i++) {
            buckets[i] = new Bucket(BUCKET_SIZE);
        }
    }

    public void push(int v) {
        final int i = v / BUCKET_SIZE;
        final int offset = v - (i * BUCKET_SIZE);
        buckets[i].values[offset] += 1;
        buckets[i].count++;
        buckets[i].sum += v; // ?
    }

    public int mean() {
        double sum = 0;
        long count = 0;
        for (int i = 0; i< buckets.length; i++) {
            sum += buckets[i].sum;
            count += buckets[i].count;
        }

        return (int) (sum / count);
    }

    public int median() {
        int l = 0;
        int r = buckets.length - 1;
        int lc = 0;
        int rc = 0;
        while (l < r) {

            if (lc > rc) {
                rc = buckets[r].count;
                r--;
            } else {
                lc += buckets[l].count;
                l++;
            }
        }

        return 0;
    }

    private static final class Bucket {
        int count = 0;
        int[] values;
        long sum = 0;

        Bucket(int capacity) {
            values = new int[capacity];
            Arrays.fill(values, 0);
        }
    }
}
