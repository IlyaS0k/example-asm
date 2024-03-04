package ru.ilyasok.asm;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.LambdaMetafactory;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringInitMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final String handlerFieldName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final String className;
    private final Method handlerMethod;
    private final String methodName;
    private final String methodDescriptor;
    private final String lambdaMethodName;

    protected ExceptionMonitoringInitMethodVisitor(int api,
                                                   MethodVisitor mv,
                                                   ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                   Class<?> handledExceptionClass,
                                                   String handlerFieldName,
                                                   String className,
                                                   Method handlerMethod,
                                                   String methodName,
                                                   String methodDescriptor,
                                                   String lambdaMethodName) {
        super(api, mv);
        this.handler = handler;
        this.className = className;
        this.handledExceptionClass = handledExceptionClass;
        this.handlerFieldName = handlerFieldName;
        this.handlerMethod = handlerMethod;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor == null ? "" : methodDescriptor;
        this.lambdaMethodName = lambdaMethodName;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInvokeDynamicInsn(
                handlerMethod.getName(),
                "(Lru/ilyasok/TestClass;)Lru/ilyasok/asm/ITryCatchHandler;",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        Type.getInternalName(LambdaMetafactory.class),
                        "metafactory",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;" +
                                "Ljava/lang/String;" +
                                "Ljava/lang/invoke/MethodType;" +
                                "Ljava/lang/invoke/MethodType;" +
                                "Ljava/lang/invoke/MethodHandle;" +
                                "Ljava/lang/invoke/MethodType;" +
                                ")Ljava/lang/invoke/CallSite;",
                        false
                ),
                new Object[]{
                        Type.getType("(Ljava/lang/Throwable;)V"),
                        new Handle(
                                Opcodes.H_INVOKESPECIAL,
                                className,
                                lambdaMethodName,
                                "(" + Type.getType(handledExceptionClass) + ";)V",
                                false
                        ),
                        Type.getType("(" + Type.getType(handledExceptionClass) + ";)V")});
        mv.visitFieldInsn(
                PUTFIELD,
                className,
                handlerMethod.getName(),
                Type.getMethodDescriptor(handlerMethod)
        );
    }


}
