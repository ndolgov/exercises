package net.ndolgov.exercise2;

/*
Create a data strucutre that operates on a sequence of numbers and provides following API:

push(elm): add a element
pop(): remove an element in FIFO order
popMax(): remove max element in the sequence
*/


import java.util.Comparator;
import java.util.PriorityQueue;

class MyCode {
    public static void main (String[] args) {
        MyCode mc = new MyCode();
        mc.push(10);
        mc.push(20);
        mc.push(30);

        System.out.println(mc.pop()); // 10
        System.out.println(mc.popMax()); // 30
        System.out.println(mc.pop()); // 20
        System.out.println(mc.pop()); // RE
    }

    public static final class Node {
        public Node(int v) {
            value = v;
        }
        Node next;
        Node prev;
        final int value;
    }

    private Node head;
    private Node tail;

    private final PriorityQueue<Node> index = new PriorityQueue<>(new Comparator<Node>() {
        public int compare(Node l, Node r) {
            return Integer.compare(r.value, l.value);
        }
    });

    public void push(int n) {
        final Node newHead = new Node(n);
        newHead.next = head;
        if (head != null) {
            head.prev = newHead;
        }
        head = newHead;
        if (tail == null) {
            tail = head;
        }

        index.offer(newHead);

    }

    public int pop() {
        if (tail == null) {
            throw new RuntimeException();
        }

        final Node popped = tail;
        tail = popped.prev;
        if (head == popped) {
            head = tail;
        }
        popped.next = null;
        popped.prev = null;

        index.remove(popped);

        return popped.value;
    }

    public int popMax() {
        if (index.isEmpty()) {
            throw new RuntimeException();
        }

        final Node max = index.poll();

        remove(max);

        return max.value;
    }

    private void remove(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        node.next = null;
        node.prev = null;

        if (tail == node) {
            tail = null;
        }
        if (head == node) {
            head = null;
        }
        // todo head/tail
    }
}
