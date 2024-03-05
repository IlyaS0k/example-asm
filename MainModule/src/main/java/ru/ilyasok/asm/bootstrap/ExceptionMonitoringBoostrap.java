package ru.ilyasok.asm.bootstrap;

import java.lang.invoke.*;

public class ExceptionMonitoringBoostrap {

    public static final String BOOTSTRAP_METHOD_NAME = "bootstrap";

    private static final ThreadLocal<MethodHandle> localMH = new ThreadLocal<>();

    public static void setMH(MethodHandle methodHandle) {
        localMH.set(methodHandle);
    }

    public static CallSite bootstrap(MethodHandles.Lookup lookup,
                                     String name,
                                     MethodType type) {
        return new ConstantCallSite(localMH.get());
    }
}
