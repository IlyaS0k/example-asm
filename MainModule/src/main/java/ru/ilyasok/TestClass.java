package ru.ilyasok;

import org.objectweb.asm.Type;
import ru.ilyasok.asm.ITryCatchHandler;

public class TestClass {

    private int xiao = 10;

    private ITryCatchHandler<RuntimeException> handler = (runtimeException) -> {
        System.out.println(xiao);
    };


    public static void main(String[] args) {
        Type.getType(RuntimeException.class);
                System.out.println(        Type.getType(RuntimeException.class));
    }

    public void TestMethod() {
        try {
            System.out.println("xiao");
        } catch (RuntimeException e) {
            handler.handle(e);
        }
    }


}
