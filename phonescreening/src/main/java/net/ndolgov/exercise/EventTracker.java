package net.ndolgov.exercise;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class EventTracker {
    public static void main(String[] args) {
        //final TreeMap<String, Integer> nameToCount = new TreeMap<>((left, right) -> Integer.compare(left));

        //final Map<String, Integer> nameToCount = new HashMap<>(100);
        isBalanced("{}[]()");
    }

    static String electionWinner(String[] votes) {
        if ((votes == null) || (votes.length == 0)) {
            throw new IllegalArgumentException();
        }

        final Map<String, Integer> nameToCount = new HashMap<>(votes.length);

        final int maxCount = count(votes, nameToCount);

        return winner(nameToCount, maxCount);
    }

    private static int count(String[] votes, Map<String, Integer> nameToCount) {
        int maxCount = 0;

        for (final String name : votes) {
            final Integer existing = nameToCount.get(name);
            if (existing == null) {
                nameToCount.put(name, 1);

                maxCount = Math.max(maxCount, 1);
            } else {
                final int incremented = existing + 1;
                nameToCount.put(name, incremented);

                maxCount = Math.max(maxCount, incremented);
            }
        }

        return maxCount;
    }

    private static String winner(Map<String, Integer> nameToCount, int maxCount) {
        String winner = null;

        for (final Map.Entry<String, Integer> entry : nameToCount.entrySet()) {
            if ((entry.getValue() == maxCount) && cmpAsc(winner, entry)) {
                winner = entry.getKey();
            }
        }

        return winner;
    }

    private static boolean cmpAsc(String winner, Map.Entry<String, Integer> entry) {
        if (winner == null) {
            return true;
        }

        return String.CASE_INSENSITIVE_ORDER.compare(winner, entry.getKey()) < 0;
    }


    private static boolean isBalanced(String value) {
        final Stack<Character> stack = new Stack<>();

        int index = 0;
        while (index < value.length()) {
            final char next = value.charAt(index++);
            switch (next) {
                case '{' :
                case '[' :
                case '(' :
                    stack.push(next); break;

                case '}' :
                case ']' :
                case ')' :
                    if (stack.pop() != next) {
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    public void addItem(String sessionId, Long productId, ConcurrentHashMap<String, Vector<Long>> shoppingCarts){
        shoppingCarts.compute(sessionId, (session, products) -> {
            if (products == null) {
                final Vector<Long> created = new Vector<>();
                created.add(productId);
                return created;
            } else {
                products.add(productId);
                return products;
            }
        });
    }
}
