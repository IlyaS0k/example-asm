package ru.ilyasok.listener;

import ru.ilyasok.service.IService;
import ru.ilyasok.service.SimpleService;

public class SimpleListener implements IListener {
    private final IService service;

    private static SimpleListener INSTANCE;

    private SimpleListener() {
        service = new SimpleService();
    }
    public static SimpleListener getInstance() {
        if (INSTANCE == null) {
            return new SimpleListener();
        }
        return INSTANCE;
    }
    @Override
    public synchronized void notifyService(String msg) {
         System.out.println("Уведомление сервиса об ошибке...");
         service.doTask(msg);
    }
}
