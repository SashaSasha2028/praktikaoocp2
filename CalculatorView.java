package view;

import java.util.Scanner;

public class CalculatorView {
    private Scanner scanner;

    public CalculatorView() {
        scanner = new Scanner(System.in);
    }

    public String getExpression() {
        System.out.println("Введите математическое выражение (например: -3234+((exp(2)*843/log(3234)-4232123)/(34+123+32+5))*3234):");
        return scanner.nextLine();
    }

    public void displayResult(double result) {
        System.out.println("Результат: " + result);
    }

    public void displayError(String message) {
        System.err.println("Ошибка: " + message);
    }
}
