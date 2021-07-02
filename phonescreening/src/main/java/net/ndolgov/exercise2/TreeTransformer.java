package net.ndolgov.exercise2;

import java.util.concurrent.atomic.AtomicInteger;

public class TreeTransformer {
    public static class Node {
        public Node left;

        public Node right;

        public int value;

        public int newValue = -1;
    }

    public static Node sumTree(Node root) {
        if (root == null) {
            return root;
        }

        visit(root, new AtomicInteger(0));

        return root;
    }

    private static void visit(Node node, AtomicInteger sumSoFar) {
        if (node == null) {
            return;
        }

        visit(node.right, sumSoFar);
        final int rightSum = sumSoFar.get();

        sumSoFar.addAndGet(node.value);
        visit(node.left, sumSoFar);

        node.newValue = rightSum + node.value;
    }

    public static void main(String[] args) {
        System.out.println("Done");
    }
}
