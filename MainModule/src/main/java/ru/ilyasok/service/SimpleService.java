package ru.ilyasok.service;

public class SimpleService implements IService{
    @Override
    public void doTask(String msg) {
       System.out.println("Сервис вызван...");
       System.out.println(msg);
    }
}
