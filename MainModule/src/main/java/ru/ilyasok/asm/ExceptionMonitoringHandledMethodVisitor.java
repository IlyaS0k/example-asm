package ru.ilyasok.asm;


import org.objectweb.asm.*;
import ru.ilyasok.asm.bootstrap.ExceptionMonitoringBoostrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringHandledMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final String handlerClassName;
    private final String handlerFieldName;
    private final String ownerClassName;
    private final String handlerMethodName;
    private final String handlerFieldDescriptor;
    private final String handlerMethodDescriptor;


    protected ExceptionMonitoringHandledMethodVisitor(int api,
                                                      MethodVisitor mv,
                                                      ITryCatchHandler<EXCEPTION_TYPE> handler, String handlerClassName,
                                                      String handlerFieldName,
                                                      String ownerClassName,
                                                      String handlerMethodName,
                                                      String handlerFieldDescriptor,
                                                      String handlerMethodDescriptor) {
        super(api, mv);
        this.handler = handler;
        this.handlerClassName = handlerClassName;
        this.ownerClassName = ownerClassName;
        this.handlerFieldName = handlerFieldName;
        this.handlerMethodName = handlerMethodName;
        this.handlerFieldDescriptor = handlerFieldDescriptor;
        this.handlerMethodDescriptor = handlerMethodDescriptor;
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
        mv.visitInsn(DUP);
        mv.visitInvokeDynamicInsn(
                handlerMethodName,
                "(" + Type.getDescriptor(ITryCatchHandler.class) + Type.getDescriptor(Throwable.class) + ")V",
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        Type.getInternalName(ExceptionMonitoringBoostrap.class),
                        "bootstrap",
                        MethodType.methodType(
                                CallSite.class,
                                MethodHandles.Lookup.class,
                                String.class,
                                MethodType.class
                        ).toMethodDescriptorString()
                )
        );
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }
}
