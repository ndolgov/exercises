package net.ndolgov.exercise2;

import java.util.Iterator;

class SeqOrder {
    public static void main (String[] args) {
        System.out.println("Hello Java");
    }

    public static class Pair {
        int offset;
        //int maxValue
    }

    public static  class Node {
        Node next;
        Node prev;
        final int offset;

        public Node(int offset) {
            this.offset = offset;
        }
    }
    public static int[] transform(int[] a) {
        final int[] result = new int[a.length];


        Node tail = null;

        int i = 0;
        int max = Integer.MIN_VALUE;
        while (i < a.length) {
            if (tail == null) {
                result[i] = 1;
            }

            if (max <= a[i]) {

            } else {
                if (tail == null) {
                    tail = new Node(i-1);
                } else {
                    Node n = new Node(i-1);
                    tail.next = n;
                    n.prev = tail;
                    tail = n;
                }
            }
        }

        return result;
    }
    public static final class ListIterator implements Iterator<Integer> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Integer next() {
            return null;
        }
    }
}



// Your last Plain Text code is saved below:
// Given a timeseries that keeps information about temperature readings for a city,

// return a timeseries that tells you, for a given day, how long has its value been the largest running value.

// For example, for temperature readings [3,5,6,2,1,4,6,9], the transformed timeseries would be [1,2,3,1,1,3,7,8]



// {(0, 3), (3, 2), (4,1)}
// {(2, 6), (3, 2), //(4,1), (7,9)}

//            |
// [3,5,6,2,1,4,6,9]


