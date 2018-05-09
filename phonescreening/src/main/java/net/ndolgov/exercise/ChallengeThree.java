package net.ndolgov.exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChallengeThree {
    private static final List<Node> NULL = Collections.emptyList();

    private static final class Node {
        private final List<Node> children;
        private final int value;

        private Node(int value, List<Node> children) {
            this.value = value;
            this.children = children;
        }

        public long sum() {
            return value + maxChildSum();
        }

        private long maxChildSum() {
            if (children.isEmpty()) {
                return 0;
            }

            long maxChildSum = Long.MIN_VALUE;
            for (final Node child : children) {
                maxChildSum = Math.max(maxChildSum, child.sum());
            }
            return maxChildSum;
        }
    }

    private static long maxSum(Node root) {
        if (root == null) {
            throw new NullPointerException();
        }

        return root.sum();
    }

    private static List<Node> newArrayList(Node... children) {
        final List<Node> nodes = new ArrayList<>();
        Collections.addAll(nodes, children);
        return nodes;
    }

    public static void main(String[] args) {
        final Node root = new Node(1, newArrayList(
                new Node(3, NULL),
                new Node(-5, NULL),
                new Node(4, NULL),
                new Node(0, newArrayList(
                        new Node(3, newArrayList(new Node(10, NULL))),
                        new Node(5, newArrayList(new Node(10, NULL))),
                        new Node(-4, newArrayList(new Node(20, NULL)))
                ))
        ));

        try {
            System.out.println("Max sum : " + maxSum(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
