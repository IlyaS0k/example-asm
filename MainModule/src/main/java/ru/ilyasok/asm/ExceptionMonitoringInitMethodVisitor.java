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
    private final String ownerClassName;
    private final String handlerMethodName;

    private final String handlerFieldDescriptor;
    private final String methodName;
    private final String methodDescriptor;
    private final String lambdaMethodName;

    protected ExceptionMonitoringInitMethodVisitor(int api,
                                                   MethodVisitor mv,
                                                   ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                   Class<?> handledExceptionClass,
                                                   String handlerFieldName,
                                                   String handlerFieldDescriptor,
                                                   String ownerClassName,
                                                   String handlerMethodName,
                                                   String methodName,
                                                   String methodDescriptor,
                                                   String lambdaMethodName) {
        super(api, mv);
        this.handler = handler;
        this.ownerClassName = ownerClassName;
        this.handledExceptionClass = handledExceptionClass;
        this.handlerFieldName = handlerFieldName;
        this.handlerFieldDescriptor = handlerFieldDescriptor;
        this.handlerMethodName = handlerMethodName;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor == null ? "" : methodDescriptor;
        this.lambdaMethodName = lambdaMethodName;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD,0);
        mv.visitInvokeDynamicInsn(
                handlerMethodName,
                "()" + Type.getDescriptor(ITryCatchHandler.class),
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
                Type.getType("(Ljava/lang/Throwable;)V"),
                new Handle(
                        Opcodes.H_INVOKESPECIAL,
                        Type.getInternalName(this.getClass()),
                        "lambda$static$0",
                        "(" + Type.getType(handledExceptionClass) + ")V",
                        false
                ),
                Type.getType("(" + Type.getType(handledExceptionClass) + ")V"));
        mv.visitFieldInsn(
                PUTFIELD,
                ownerClassName,
                handlerFieldName,
                handlerFieldDescriptor
        );
    }
}
