package ru.ilyasok;

import ru.ilyasok.asm.ITryCatchHandler;

public class MyClass {
    public static ITryCatchHandler<RuntimeException> HANDLER = (runtimeException) -> {
        System.out.println("xiao");
    };
}
