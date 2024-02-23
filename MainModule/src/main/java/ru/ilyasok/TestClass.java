package ru.ilyasok;

import ru.ilyasok.asm.ITryCatchHandler;

public class TestClass {
    private ITryCatchHandler<RuntimeException> handler ;

    public void TestMethod() {
        handler = Throwable::printStackTrace;
        try {
            throw new RuntimeException();
        }
        catch (RuntimeException e) {
            handler.handle(e);
        }
    }

}
