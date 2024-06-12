package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class ExpressionResolver {

    public static void main(String[] args) {
        String expression = "sin(x) + (2 * 3) - (4 / 2)";
        Map<Character, Double> variables = new HashMap<>();
        variables.put('x', null);

        Scanner scanner = new Scanner(System.in);
        for (char variable : variables.keySet()) {
            System.out.print("Enter the value for variable " + variable + ": ");
            double value = scanner.nextDouble();
            variables.put(variable, value);
        }

        try {
            double result = evaluateExpression(expression, variables);
            System.out.println("Result: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static double evaluateExpression(String expression, Map<Character, Double> variables) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int length = expression.length();
        boolean expectOperand = true;

        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                continue;
            }

            if (Character.isLetter(currentChar)) {
                StringBuilder function = new StringBuilder();
                while (i < length && Character.isLetter(expression.charAt(i))) {
                    function.append(expression.charAt(i));
                    i++;
                }
                i--;

                String functionName = function.toString();
                if (isFunction(functionName)) {
                    operators.push(getFunctionSymbol(functionName));
                    expectOperand = false;
                } else {
                    if (variables.containsKey(currentChar)) {
                        operands.push(variables.get(currentChar));
                    } else {
                        throw new IllegalArgumentException("Undefined variable: " + currentChar);
                    }
                    expectOperand = false;
                }
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
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
                while (!operators.isEmpty() && operators.peek() != '(') {
                    char operator = operators.pop();
                    if (isFunctionOperator(operator)) {
                        operands.push(applyFunction(operator, operands.pop()));
                    } else {
                        operands.push(applyOperator(operator, operands.pop(), operands.pop()));
                    }
                }
                if (!operators.isEmpty() && operators.peek() == '(') {
                    operators.pop();
                } else {
                    throw new IllegalArgumentException("Invalid expression");
                }
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
                        char operator = operators.pop();
                        if (isFunctionOperator(operator)) {
                            operands.push(applyFunction(operator, operands.pop()));
                        } else {
                            operands.push(applyOperator(operator, operands.pop(), operands.pop()));
                        }
                    }
                    operators.push(currentChar);
                    expectOperand = true;
                }
            } else {
                throw new IllegalArgumentException("Invalid character in expression");
            }
        }

        while (!operators.isEmpty()) {
            char operator = operators.pop();
            if (isFunctionOperator(operator)) {
                operands.push(applyFunction(operator, operands.pop()));
            } else {
                operands.push(applyOperator(operator, operands.pop(), operands.pop()));
            }
        }

        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return operands.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean isFunction(String functionName) {
        return functionName.equals("sin") || functionName.equals("cos") || functionName.equals("tan") || functionName.equals("sqrt");
    }

    private static boolean isFunctionOperator(char operator) {
        return operator == 's' || operator == 'c' || operator == 't' || operator == 'q';
    }

    private static char getFunctionSymbol(String functionName) {
        return switch (functionName) {
            case "sin" -> 's';
            case "cos" -> 'c';
            case "tan" -> 't';
            case "sqrt" -> 'q';
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case 's', 'c', 't', 'q' -> 3;
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

    private static double applyFunction(char operator, double operand) {
        return switch (operator) {
            case 's' -> Math.sin(operand);
            case 'c' -> Math.cos(operand);
            case 't' -> Math.tan(operand);
            case 'q' -> Math.sqrt(operand);
            default -> 0;
        };
    }
}