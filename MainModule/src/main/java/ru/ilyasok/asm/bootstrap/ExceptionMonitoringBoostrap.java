package ru.ilyasok.asm.bootstrap;

import ru.ilyasok.asm.ITryCatchHandler;

import java.lang.invoke.*;

public class ExceptionMonitoringBoostrap {



    public static CallSite bootstrap(MethodHandles.Lookup lookup,
                                     String name,
                                     MethodType type) throws Exception {
        MethodHandle mh = MethodHandles.lookup().findVirtual(
                ITryCatchHandler.class,
                "handle",
                MethodType.methodType(void.class, Throwable.class));
        return new ConstantCallSite(mh);
    }
}
