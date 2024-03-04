package ru.ilyasok;

import org.objectweb.asm.Type;
import ru.ilyasok.asm.ITryCatchHandler;

import java.util.ArrayList;
import java.util.List;

public class TestClass {

    private List l;

    private  ITryCatchHandler<RuntimeException> handler = (runtimeException) -> {
        System.out.println(runtimeException.getMessage());
    };


    public static void main(String[] args) {
        List l = new ArrayList();
        l.clear();

    }




}
