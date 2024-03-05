package ru.ilyasok;

import ru.ilyasok.asm.ITryCatchHandler;

import java.util.ArrayList;


public class TestClass {

   private ITryCatchHandler<RuntimeException> myClass;
    public TestClass() {
        myClass = new MyClass();

    }


    public static void main(String[] args) {

    }



}
