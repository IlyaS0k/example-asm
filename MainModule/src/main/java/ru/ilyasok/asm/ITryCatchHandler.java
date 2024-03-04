package ru.ilyasok.asm;

@FunctionalInterface
public interface ITryCatchHandler<EXCEPTION_TYPE extends Throwable> {
    void handle(EXCEPTION_TYPE exception);
}
