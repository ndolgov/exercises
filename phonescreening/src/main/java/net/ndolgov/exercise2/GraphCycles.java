package net.ndolgov.exercise2;

import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

class GraphCycles {
    public static void main(String[] args) {
        //testOneCycle();
        //testNoCycle();
        testCycles();
    }


    enum State {NEW, VISITING, VISITED};

    public static class Node {
        public Node(List<Edge> edges) {
            this.edges = edges;
        }

        final List<Edge> edges;
        State state = State.NEW;
    }

    public static class Edge {
        public Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
        }

        final Node from;
        final Node to;
    }

    private static int time = 0;
    //private static final Map<Node, Integer>
    //final int[] order = new int[graph.size()];

    public static void hasCycle(List<Node> graph) {
        if (graph.isEmpty()) {
            return;
        }

        visit(graph.get(0));

//     Arrays.fill(order, -1);

//     final Stack<Node> nodes = new Stack<>();
//     nodes.push(graph.get(0));

//     while (!nodes.isEmpty()) {
//       final Node next = nodes.pop();
//       if (node.state == VISITING) {

//         return true;
//       }
//       if (node.state == NEW) {
//         node.state = VISITING;
//         next.edges.stream.filter(e -> e.from == node).forEach(e -> nodes.push(e.to));
//         node.state = VISITED;
//       }

//     }


    }

    private static void visit(Node node) {

        if (node.state == State.VISITING) {
            System.out.println("Detected cycle of length: " + time + " at node " + node);
            //return true;
        }

        if (node.state == State.NEW) {
            node.state = State.VISITING;

            time++;

            for (Edge e : node.edges) {
                if (e.from == node) {
                    visit(e.to);
                    // if (visit(e.to)) {
                    //   //return true;
                    // }
                }
            }
            // next.edges.stream.filter(e -> e.from == node).map(e -> visit(e.to)).collect();
            node.state = State.VISITED;
        }

        //return false;
    }

//   public static void testOneCycle() {
//     Node node1 = new Node(new ArrayList<Edge>());
//     Node node2 = new Node(new ArrayList<Edge>());
//     Node node3 = new Node(new ArrayList<Edge>());
//     Node node4 = new Node(new ArrayList<Edge>());

//     node1.edges.add(new Edge(node1, node2));
//     node2.edges.add(new Edge(node2, node3));
//     node3.edges.add(new Edge(node3, node4));
//     node4.edges.add(new Edge(node4, node1));

//     ArrayList<Node> graph = new ArrayList<Node>();
//     graph.add(node1);
//     graph.add(node2);
//     graph.add(node3);
//     graph.add(node4);


//     System.out.println(hasCycle(graph));
//   }

//   public static void testNoCycle() {
//     Node node1 = new Node(new ArrayList<Edge>());
//     Node node2 = new Node(new ArrayList<Edge>());
//     Node node3 = new Node(new ArrayList<Edge>());
//     Node node4 = new Node(new ArrayList<Edge>());

//     node1.edges.add(new Edge(node1, node2));
//     node2.edges.add(new Edge(node1, node3));
//     node3.edges.add(new Edge(node1, node4));

//     ArrayList<Node> graph = new ArrayList<Node>();
//     graph.add(node1);
//     graph.add(node2);
//     graph.add(node3);
//     graph.add(node4);


//     System.out.println(hasCycle(graph));
//   }

    public static void testCycles() {
        Node node1 = new Node(new ArrayList<Edge>());
        Node node2 = new Node(new ArrayList<Edge>());
        Node node3 = new Node(new ArrayList<Edge>());
        Node node4 = new Node(new ArrayList<Edge>());

        node1.edges.add(new Edge(node1, node3)); //
        node1.edges.add(new Edge(node1, node2));
        node2.edges.add(new Edge(node2, node3));
        node3.edges.add(new Edge(node3, node4));
        node4.edges.add(new Edge(node4, node1));

        ArrayList<Node> graph = new ArrayList<Node>();
        graph.add(node1);
        graph.add(node2);
        graph.add(node3);
        graph.add(node4);


        hasCycle(graph);
    }
}

