package net.ndolgov.exercise2;

import java.util.*;

public class Calculator {

    public static void main(String [] args) {
        // you can write to stdout for debugging purposes, e.g.
        //System.out.println(calculate("2+3*5+3"));
        System.out.println(calculate("3*2*5*2*1"));
        System.out.println(calculate("1+3+5+7"));
        System.out.println(calculate("2+3*5+3*4+6*2*1*5+3*4*9"));
    }
    public static enum Ops {PLUS, STAR};
    public static int calculate(String expr) {
        if (expr == null || expr.equals("")) {
            return -1;
        }

        // 2+3*5+3*4+6*2*1*5+3*4*9
        // 3*2*5*2*1
        // 1+3+5+7


        final Stack<Integer> operands = new Stack<>();
        final Stack<Ops> operators = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            final char token = expr.charAt(i);

            switch (token) {
                case '+':
                    operators.push(Ops.PLUS);
                    //final int left = stack.push(token);
                    //final int right = stack.push(token);
                    break;

                case '*':
                    operators.push(Ops.STAR);
                    break;

                default:
                    operands.push(Integer.parseInt(""+token));

                    if (!operators.isEmpty() && operators.peek() == Ops.STAR) {
                        operands.push(operands.pop() * operands.pop());
                        operators.pop();
                    }
                    break;
            }
        }

        while (!operators.isEmpty()) {
            operands.push(operands.pop() + operands.pop()); // todo + only
            operators.pop();
        }

        return operands.pop();
    }
}
