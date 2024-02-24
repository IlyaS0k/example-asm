package ru.ilyasok;

import ru.ilyasok.asm.ITryCatchHandler;

public class TestClass {

    private ITryCatchHandler<RuntimeException> handler = (runtimeException) -> {
        System.out.println(runtimeException.getMessage());};


    public static void main(String[] args) {

    }

    public void TestMethod() {

        try {
            throw new RuntimeException();
        }
        catch (RuntimeException e) {
            handler.handle(e);
        }
    }

}
