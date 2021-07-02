package net.ndolgov.exercise2;

import java.util.*;

// I1 = 4, 1, 0, 2
// I2 = 5, 6
// I3 = 1
class SeqIterator {
    public static void main (String[] args) {
        ArrayList<Integer> a = new ArrayList<>();
        a.add(4);
        a.add(1);
        a.add(0);
        a.add(2);
        ArrayList<Integer> b = new ArrayList<>();
        b.add(5);
        b.add(6);
        ArrayList<Integer> c = new ArrayList<>();
        c.add(1);
        ArrayList<Iterator<Integer>> its = new ArrayList<>();
        its.add(a.iterator());
        its.add(b.iterator());
        its.add(c.iterator());

        Iterator<Integer> it = merge(its);
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        new Random();

    }

    public static Iterator<Integer> merge (List<Iterator<Integer>> its) {
        return new ListIterator(its);
    }

    public static final class ListIterator implements Iterator<Integer> {
        private final List<Iterator<Integer>> its;
        private int itIndex = 0;

        public ListIterator(List<Iterator<Integer>> its) {
            this.its = its;
        }

        public boolean hasNext() {
            if (its.isEmpty()) {
                return false;
            }

            if (!its.get(itIndex).hasNext()) {
                its.remove(itIndex);
                if (its.isEmpty()) {
                    return false;
                }

                itIndex = (itIndex + 1) % its.size();
                return hasNext();
            }

            return true;
        }

        public Integer next() {
            final Integer nextV = its.get(itIndex).next();
            itIndex = (itIndex + 1) % its.size();
            return nextV;
        }
    }
}

// Given K integer iterators:
// I1 = 4, 1, 0, 2
// I2 = 5, 6
// I3 = 1
// return integer iterator which traverses I1...IK like so:
// I = 4, 5, 1, 1, 6, 0, 2


