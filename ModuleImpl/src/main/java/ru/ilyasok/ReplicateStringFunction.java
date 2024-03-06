package ru.ilyasok;

public class ReplicateStringFunction implements IReplicateStringFunction {

    private final int times;

    public ReplicateStringFunction(int times) {
        this.times = times;
    }
    @Override
    public String apply(String s) {
        if (s == null) throw new NullPointerException();
        return s.repeat(times);
    }
}
