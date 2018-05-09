package net.ndolgov.exercise;

public final class Calculator {
    private final String input;
    private int pos = 0;

    public Calculator(String input) {
        this.input = input;
    }

    public static void main(String[] args) {
        final String input = "1 + 233 / 233";//args[0];
        final Calculator s = new Calculator(input);
        System.out.println(s.eval());
    }


    public int eval() {
        pos = nextWhiteSpace(0);
        final int left = Integer.valueOf(input.substring(0, pos));

        int r = left;
        while (pos < input.length()) {
            r = calc(r);
        }
        return r;
    }

    private int calc(int left) {
        final int e1 = nextWhiteSpace(pos);
        final Character op = input.charAt(e1-1);
        pos = e1;

        return calc(left, op);
    }

    private int calc(int left, Character op) {
        final int e2 = nextWhiteSpace(pos);
        final int right = Integer.valueOf(input.substring(pos+1, e2));
        pos = e2;
        switch (op) {
            case '+' :
                return left + right;

            case '-' :
                return left - right;

            case '*' :
                return left * right;

            case '/' :
                return left / right; //todo

            default: throw new IllegalArgumentException("OP=" + op);
        }
    }

    private int nextWhiteSpace(int lastWhiteSpaceIndex) {
        for (int i = lastWhiteSpaceIndex + 1; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                return i;
            }
        }
        return input.length();
    }
}