package ru.ilyasok.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringHandledMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {
    private final String handlerFieldName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final Class<?> handlerClass;
    private final String className;
    private final Method handlerMethod;


    protected ExceptionMonitoringHandledMethodVisitor(int api,
                                                      MethodVisitor mv,
                                                      ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                      Class<?> handledExceptionClass,
                                                      String handlerFieldName,
                                                      String className,
                                                      Method handlerMethod) {
        super(api, mv);
        this.handler = handler;
        this.className = className;
        this.handledExceptionClass = handledExceptionClass;
        this.handlerClass = handledExceptionClass;
        this.handlerFieldName = handlerFieldName;
        this.handlerMethod = handlerMethod;
    }

    private final Label TRY_START = new Label();
    private final Label TRY_END = new Label();
    private final Label HANDLER = new Label();

    @Override
    public void visitCode() {

        String handledExceptionType = Type.getInternalName(RuntimeException.class);
        mv.visitTryCatchBlock(TRY_START, TRY_END, HANDLER, handledExceptionType);
        mv.visitLabel(TRY_START);
        mv.visitCode();
    }


    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

        mv.visitLabel(TRY_END);
        mv.visitLabel(HANDLER);
        mv.visitInsn(DUP);
        mv.visitFieldInsn(
                GETFIELD,
                className,
                handlerFieldName,
                Type.getDescriptor(ITryCatchHandler.class)
        );
        mv.visitMethodInsn(
                INVOKEINTERFACE,
                Type.getDescriptor(handlerClass),
                handlerMethod.getName(),
                Type.getDescriptor(handledExceptionClass),
                true);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }
}
