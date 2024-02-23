package ru.ilyasok.asm;

@FunctionalInterface
public interface ITryCatchHandler<T extends Throwable> {
    void handle(T throwable);
}
