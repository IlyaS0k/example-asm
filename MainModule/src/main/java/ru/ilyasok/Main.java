package ru.ilyasok;

import org.objectweb.asm.Type;
import ru.ilyasok.asm.ITryCatchHandler;

public class Main {

    public static void main(String[] args) {
        System.out.println(Type.getDescriptor(ITryCatchHandler.class));
    }



}
