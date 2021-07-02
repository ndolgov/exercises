package net.ndolgov.exercise2;

import java.util.*;

/*
class LRUCache {

    public LRUCache(int capacity) {

    }

    public int get(int key) {

    }

    public void put(int key, int value) {

    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */



class LRU {
    public static class Node {
        public Node (int k, int v) {
            value = v;
            key = k;
            next = null;
            previous = null;
        }

        public Node (int k, int v, Node n) {
            value = v;
            key = k;
            next = n;
            previous = null;
        }

        final int value;

        final int key;

        Node next;

        Node previous;
    }


    public static class LRUCache {
        private Node head = null;

        private Node tail = null;

        private final Map<Integer, Node> keyToNode;

        private final int capacity;

        public LRUCache(int capacity) {
            keyToNode = new HashMap<>(capacity);
            this.capacity = capacity;
        }

        public int get(int key) {
            final Node existing = keyToNode.get(key);
            if (existing == null) {
                return -1;
            }


            deleteFromList(existing);
            prepend(existing);


            return existing.value;
        }

        public void put(int key, int value) {
            if (keyToNode.size() >= capacity) {
                final Node oldest = tail;
                final Node newTail = tail == null ? null : tail.previous;

                delete(oldest);

                tail = newTail;
            }


            final Node existing = keyToNode.get(key);
            if (existing != null) {
                delete(existing);

            }

            final Node newNode = prepend(new Node(key, value));

            keyToNode.put(key, newNode);
        }

        private void delete(Node node) {
            if (node == null) {
                return;
            }

            keyToNode.remove(node.key);
            deleteFromList(node);
        }

        private Node deleteFromList(Node node) {
            final Node prev = node.previous;
            final Node next = node.next;

            if (prev != null) {
                prev.next = next;
            }

            if (next != null) {
                next.previous = prev;
            }

            return node;
        }

        private Node prepend(Node newHead) {
            final Node oldHead = head;

            if (oldHead != null) {
                oldHead.previous = newHead;
            } else {
                tail = newHead;
            }
            newHead.next = oldHead;

            head = newHead;


            return newHead;
        }
    }

    public static void main(String[] args) {
        final LRUCache cache = new LRUCache(2);
        System.out.println(cache.get(10));

        cache.put(20, 200);
        cache.put(30, 300);

        System.out.println(cache.get(20));
        System.out.println(cache.get(30));

        cache.put(40, 400);
        System.out.println(cache.get(20)); // -1
        System.out.println(cache.get(30));
        System.out.println(cache.get(40));


        //System.out.println("");
    }
}

