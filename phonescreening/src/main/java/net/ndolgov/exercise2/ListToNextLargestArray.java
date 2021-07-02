package net.ndolgov.exercise2;

import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.


Input: 2, 1, 0, 4, 7

Output:4, 4, 4, 7, -1


  O:

  : >2

  4
  0
  1
  2



4


Input:  [2 -> 7 -> 4 -> 3 -> 5 -> 2 -> 4 -> 45]


Output: [7,   45,  5,   5,   45,  4,   45,  -1]


*/
class ListToNextLargestArray {
    public static void main(String[] args) {

        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(7);
        list.add(4);
        list.add(3);
        list.add(5);
        list.add(2);
        list.add(4);
        list.add(45);
        System.out.println(Arrays.toString(nextBig(list)));
    }

    public static class Node {
        int value;
        Node next;
    }

    public static int[] nextBig(List<Integer> list) {
        if (list.isEmpty()) {
            return new int[0];
        }

        final int[] output = new int[list.size()];
        int j = 0;
        //int curr = 0;

        final Stack<Integer> stack = new Stack<>();
        stack.push(list.get(0));

        int i = 1;
        while (i < list.size()) {
            final int next = list.get(i);
//        if (next > stack().peek()) {

            while (!stack.isEmpty() && (next > stack.peek())) {
                output[j++] = next;
            }
            stack.push(next);



//         } else {
//           stack.push(list.get(i));
//         }
            i++;

        }

        //int cur


        return output;
    }
}



