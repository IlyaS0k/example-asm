package ru.ilyasok.asm;

import org.objectweb.asm.*;
import ru.ilyasok.asm.bootstrap.ExceptionMonitoringBoostrap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {
    private static final String handleMethodName = "handle";
    private final Class<EXCEPTION_TYPE> exceptionClass;
    private final String wrappedMethodName;
    private final String wrappedMethodDescriptor;
    private final String handleMethodDescriptor;
    private final MethodHandle mhBindToHandler;

    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           Class<EXCEPTION_TYPE> exceptionClass,
                                           String className,
                                           ClassLoader classLoader,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           String wrappedMethodName,
                                           String wrappedMethodDescriptor) {
        super(api, cv);
        this.exceptionClass = exceptionClass;
        this.wrappedMethodName = wrappedMethodName;
        this.wrappedMethodDescriptor = wrappedMethodDescriptor;
        this.handleMethodDescriptor = "("+ Type.getDescriptor(Throwable.class) + ")V";
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType mt = MethodType.methodType(void.class, Throwable.class);
            MethodHandle mh = lookup.findVirtual(ITryCatchHandler.class, handleMethodName, mt);
            mhBindToHandler = mh.bindTo(handler);
            ExceptionMonitoringBoostrap.setMH(mhBindToHandler, classLoader, className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null &&
                ((wrappedMethodDescriptor == null && name.equals(wrappedMethodName)) ||
                        (name.equals(wrappedMethodName) && desc.equals(wrappedMethodDescriptor)))
        ) {
            return new ExceptionMonitoringHandledMethodVisitor<>(
                    api,
                    mv,
                    exceptionClass,
                    handleMethodName,
                    handleMethodDescriptor
            );
        }
        return mv;
    }

}
