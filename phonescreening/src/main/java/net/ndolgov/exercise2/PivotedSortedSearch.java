package net.ndolgov.exercise2;

public class PivotedSortedSearch {
    public static void main(String[] args) {

        int[] a = new int[] {4,5,6,7,0,1,2};
        System.out.println(search(a, 3, 0, a.length - 1));
        System.out.println(search(a, 0, 0, a.length - 1));

    }


    public static int search(int[] a, int t, int l, int r) {
        if (l >= r) { // ==?
            return -1;
        }

        final int m = (l + r) / 2;
        if (a[m] == t) {
            return m;
        }

        if (a[l] < a[m]) {
            if (a[l] <= t && t < a[m]) {
                return search(a, t, l, m);
            } else {
                return search(a, t, m, r);
            }
        } else {
            return search(a, t, l, m);
        }
    }
}

/*abstract

You are given an integer array nums sorted in ascending order, and an integer target.

Suppose that nums is rotated at some pivot unknown to you beforehand (i.e., [0,1,2,4,5,6,7] might become [4,5,6,7,0,1,2]).

If target is found in the array return its index, otherwise, return -1.

Example 1:

Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4
Example 2:

Input: nums = [4,5,6,7,0,1,2], target = 3
Output: -1
Example 3:

Input: nums = [1], target = 0
Output: -1

[3,4,5,6,7,8,9,0,1,2] target=0

[6,7,8]
[6,0,5]
[]
*/