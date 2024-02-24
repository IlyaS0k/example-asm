package ru.ilyasok;

public class RunTimeLoadModule implements IRunTimeLoadModule {

    @Override
    public void invoke() {
        throw new RuntimeException();
    }

}
