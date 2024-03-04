package ru.ilyasok.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {

    private static final String initMethodName = "<init>";
    private static final String handlerFieldName = "handler$excptnmonitor$";
    private static final String lambdaMethodNamePrefix = "lambda$excptnmonitor$";
    private final String lambdaMethodName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final Class<?> handlerClass;
    private final Method handlerMethod;
    private final String className;
    private final String methodName;
    private final String methodDescriptor;

    @SuppressWarnings("unchecked")
    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           String className,
                                           String methodName,
                                           String methodDescriptor) {
        super(api, cv);
        this.handler = handler;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.className = className;
        this.handledExceptionClass = (Class<EXCEPTION_TYPE>)
                ((ParameterizedType) this.getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        this.handlerClass = handler.getClass();
        this.handlerMethod = Arrays.stream(handler.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().orElseThrow();
        String descriptorAsStr = methodDescriptor == null ? "" : methodDescriptor;
        this.lambdaMethodName = lambdaMethodNamePrefix + methodName + "$" + descriptorAsStr;
    }


    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && (
                                (methodDescriptor == null && name.equals(methodName)) ||
                                        (name.equals(methodName) && desc.equals(methodDescriptor))
                        )
        ) {
            return new ExceptionMonitoringHandledMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handlerClass,
                    handlerFieldName,
                    className,
                    handlerMethod);
        }

        if (mv != null && initMethodName.equals(name)) {
            return new ExceptionMonitoringInitMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handledExceptionClass,
                    handlerFieldName,
                    className,
                    handlerMethod,
                    methodName,
                    methodDescriptor,
                    lambdaMethodName
            );
        }
        return mv;
    }
}
