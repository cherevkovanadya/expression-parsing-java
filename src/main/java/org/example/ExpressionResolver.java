package org.example;

import java.util.Stack;

public class ExpressionResolver {

    public static void main(String[] args) {
        String expression = "(1 + ((-2) * 3) - (4 / 2) - (-5))";

        try {
            double result = evaluateExpression(expression);
            System.out.println("Result: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static double evaluateExpression(String expression) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int length = expression.length();
        boolean expectOperand = true;

        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                continue;
            }

            if (Character.isDigit(currentChar) || currentChar == '.') {
                StringBuilder number = new StringBuilder();
                while (i < length && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                i--;
                operands.push(Double.parseDouble(number.toString()));
                expectOperand = false;
            } else if (currentChar == '(') {
                operators.push(currentChar);
                expectOperand = true;
            } else if (currentChar == ')') {
                while (operators.peek() != '(') {
                    operands.push(applyOperator(operators.pop(), operands.pop(), operands.pop()));
                }
                operators.pop(); // pop '('
                expectOperand = false;
            } else if (isOperator(currentChar)) {
                if (currentChar == '-' && expectOperand) {
                    StringBuilder number = new StringBuilder();
                    number.append(currentChar);
                    i++;
                    while (i < length && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        number.append(expression.charAt(i));
                        i++;
                    }
                    i--;
                    operands.push(Double.parseDouble(number.toString()));
                    expectOperand = false;
                } else {
                    while (!operators.isEmpty() && precedence(currentChar) <= precedence(operators.peek())) {
                        operands.push(applyOperator(operators.pop(), operands.pop(), operands.pop()));
                    }
                    operators.push(currentChar);
                    expectOperand = true;
                }
            } else {
                throw new IllegalArgumentException("Invalid character in expression");
            }
        }

        while (!operators.isEmpty()) {
            operands.push(applyOperator(operators.pop(), operands.pop(), operands.pop()));
        }

        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return operands.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }

    private static double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                yield a / b;
            }
            default -> 0;
        };
    }
}