package ru.ilyasok;


import ru.ilyasok.asm.IBytecodeTryCatchWrapper;
import ru.ilyasok.asm.ITryCatchHandler;

public class MyClass implements ITryCatchHandler<RuntimeException> {

    @Override
    public void handle(RuntimeException exception) {

    }
}
