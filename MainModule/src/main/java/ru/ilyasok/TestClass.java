package ru.ilyasok;

import org.objectweb.asm.Type;
import ru.ilyasok.asm.ITryCatchHandler;

public class TestClass {

    private int xiao = 10;

    private static ITryCatchHandler<RuntimeException> handler = (runtimeException) -> {
        System.out.println("xiao");
    };


    public static void main(String[] args) {
        handler.handle(new RuntimeException());
    }




}
