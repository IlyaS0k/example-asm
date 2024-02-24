package ru.ilyasok.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {

    private static final String handlerFieldName = "_handler$";
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final Class<?> handlerClass;
    private final Method handlerMethod;
    private final String className;
    private final String methodName;
    private int lambdaMaxIndex = 0;

    @SuppressWarnings("unchecked")
    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           String className,
                                           String methodName) {
        super(api, cv);
        this.handler = handler;
        this.methodName = methodName;
        this.className = className;
        this.handledExceptionClass = (Class<EXCEPTION_TYPE>)
                ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.handlerClass = handler.getClass();
        this.handlerMethod = handler.getClass().getMethods()[0];
    }


    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        if (name.startsWith("lambda$new$") && ((access & ACC_SYNTHETIC) != 0) ) {
            lambdaMaxIndex++;
        }
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && name.equals(methodName)) {
            return new ExceptionMonitoringHandledMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handlerClass,
                    handlerFieldName,
                    className
            );
        }

        if (mv != null && "<init>".equals(name)) {
            return new ExceptionMonitoringInitMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handledExceptionClass,
                    handlerFieldName,
                    className
            );
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(
                ACC_PRIVATE | ACC_FINAL | ACC_SYNTHETIC,
                "lambda$new$" + lambdaMaxIndex,
                Type.getMethodDescriptor(handlerMethod),
                null,
                null
                );
        if (mv != null) {
            mv.visitEnd();
        }
        cv.visitEnd();
    }
}
