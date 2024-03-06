package ru.ilyasok;

public class Divisor implements IDivisor {
    @Override
    public int divide(int a, int b) {
        if (b == 0) throw new RuntimeException("division by zero!");
        return a / b;
    }
}
