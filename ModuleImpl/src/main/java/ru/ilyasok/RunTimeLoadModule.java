package ru.ilyasok;

public class RunTimeLoadModule implements IRunTimeLoadModule {
    public MyClass myClass = new MyClass();

    @Override
    public void invoke() {
        System.out.println(myClass.xiao);
    }

}
