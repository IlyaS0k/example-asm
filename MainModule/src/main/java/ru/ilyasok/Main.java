package ru.ilyasok;

import org.objectweb.asm.util.ASMifier;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ASMifier.main(new String[] { "ru.ilyasok.TestClass" });
    }

}
