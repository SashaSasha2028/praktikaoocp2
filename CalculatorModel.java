package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CalculatorModel {
    public double calculate(String expression) throws IllegalArgumentException {
        List<String> tokens = tokenize(expression);
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        StringBuilder function = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (Character.isLetter(c)) {
                function.append(c);
            } else if (function.length() > 0) {
                if (c == '(') {
                    tokens.add(function.toString());
                    tokens.add("(");
                    function.setLength(0);
                } else {
                    throw new IllegalArgumentException("Некорректное имя функции: " + function.toString());
                }
            } else if (Character.isDigit(c) || c == '.' || (c == '-' && (i == 0 || 
                isOperator(String.valueOf(expression.charAt(i-1))) || expression.charAt(i-1) == '(')) {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                
                if (c == '*' && i + 1 < expression.length() && expression.charAt(i+1) == '*') {
                    tokens.add("**");
                    i++;
                } else if (c == '/' && i + 1 < expression.length() && expression.charAt(i+1) == '/') {
                    tokens.add("//");
                    i++;
                } else if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        
        if (number.length() > 0) {
            tokens.add(number.toString());
        }
        
        return tokens;
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || 
               token.equals("/") || token.equals("^") || token.equals("//") || 
               token.equals("**") || token.equals("!");
    }

    private boolean isFunction(String token) {
        return token.equals("exp") || token.equals("log");
    }

    private int getPrecedence(String operator) {
        switch (operator) {
            case "**":
            case "^":
                return 5;
            case "!":
                return 4;
            case "exp":
            case "log":
                return 4;
            case "*":
            case "/":
            case "//":
                return 3;
            case "+":
            case "-":
                return 2;
            default:
                return 0;
        }
    }

    private List<String> infixToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        
        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (isFunction(token)) {
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop(); // Remove "(" from stack
                
                // Если на вершине стека функция, добавляем ее в выход
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop());
                }
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && getPrecedence(stack.peek()) >= getPrecedence(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }
        
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        
        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();
        
        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (isFunction(token)) {
                double a = stack.pop();
                
                switch (token) {
                    case "exp":
                        stack.push(Math.exp(a));
                        break;
                    case "log":
                        stack.push(Math.log(a) / Math.log(2));
                        break;
                }
            } else {
                if (token.equals("!")) {
                    int a = (int)Math.round(stack.pop());
                    if (a < 0) throw new IllegalArgumentException("Факториал отрицательного числа");
                    long fact = 1;
                    for (int i = 2; i <= a; i++) {
                        fact *= i;
                    }
                    stack.push((double)fact);
                } else {
                    double b = stack.pop();
                    double a = stack.pop();
                    
                    switch (token) {
                        case "+":
                            stack.push(a + b);
                            break;
                        case "-":
                            stack.push(a - b);
                            break;
                        case "*":
                            stack.push(a * b);
                            break;
                        case "/":
                            stack.push(a / b);
                            break;
                        case "//":
                            stack.push((double)((int)(a / b)));
                            break;
                        case "**":
                        case "^":
                            stack.push(Math.pow(a, b));
                            break;
                    }
                }
            }
        }
        
        return stack.pop();
    }
}
