package controller;

import model.CalculatorModel;
import view.CalculatorView;

public class CalculatorController {
    private CalculatorModel model;
    private CalculatorView view;

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
    }

    public void process() {
        String expression = view.getExpression();
        
        try {
            validateExpression(expression);
            double result = model.calculate(expression);
            view.displayResult(result);
        } catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
        }
    }

    private void validateExpression(String expression) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Выражение не может быть пустым");
        }
        
        // Проверка, что выражение начинается и заканчивается числом или скобкой
        String trimmed = expression.replaceAll("\\s+", "");
        if (!trimmed.matches("^(-?\\d+|\\().*(\\)|\\d+)$")) {
            throw new IllegalArgumentException("Уравнение должно начинаться и заканчиваться числом или скобкой");
        }
        
        // Проверка на количество операторов (не более 14 для 15 слагаемых)
        long operatorCount = trimmed.chars()
            .filter(c -> "+-*/^!".indexOf(c) != -1)
            .count();
        
        // Учитываем ** и //
        operatorCount += countOccurrences(trimmed, "**");
        operatorCount += countOccurrences(trimmed, "//");
        
        if (operatorCount > 14) {
            throw new IllegalArgumentException("Количество операций превышает допустимый лимит (15 слагаемых)");
        }
        
        // Проверка скобок
        int balance = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) break;
        }
        
        if (balance != 0) {
            throw new IllegalArgumentException("Несбалансированные скобки в выражении");
        }
        
        // Проверка функций
        if (!trimmed.matches("(exp|log)\\(.*\\)")) {
            throw new IllegalArgumentException("Некорректное использование функций");
        }
    }

    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
