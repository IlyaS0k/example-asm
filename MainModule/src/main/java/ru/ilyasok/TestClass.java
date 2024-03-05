package ru.ilyasok;

import ru.ilyasok.asm.ITryCatchHandler;

import java.util.ArrayList;


public class TestClass {

   private ITryCatchHandler<RuntimeException> myClass = new ITryCatchHandler<RuntimeException>() {
       @Override
       public void handle(RuntimeException exception) {
           System.out.println("23142");
       }
   };
    public TestClass() {


    }


    public static void main(String[] args) {

    }



}
