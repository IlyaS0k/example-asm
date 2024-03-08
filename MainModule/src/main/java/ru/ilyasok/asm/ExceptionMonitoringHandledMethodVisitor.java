package ru.ilyasok.asm;


import org.objectweb.asm.*;
import ru.ilyasok.asm.bootstrap.ExceptionMonitoringBoostrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringHandledMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    Class<EXCEPTION_TYPE> exceptionClass;
    private final String handleMethodName;
    private final String handleMethodDescriptor;


    protected ExceptionMonitoringHandledMethodVisitor(int api,
                                                      MethodVisitor mv,
                                                      Class<EXCEPTION_TYPE> exceptionClass,
                                                      String handleMethodName,
                                                      String handleMethodDescriptor) {
        super(api, mv);
        this.handleMethodName = handleMethodName;
        this.handleMethodDescriptor = handleMethodDescriptor;
        this.exceptionClass = exceptionClass;
    }

    private final Label TRY_START = new Label();
    private final Label TRY_END = new Label();
    private final Label HANDLER = new Label();

    @Override
    public void visitCode() {

        String handledExceptionType = Type.getInternalName(exceptionClass);
        mv.visitTryCatchBlock(TRY_START, TRY_END, HANDLER, handledExceptionType);
        mv.visitLabel(TRY_START);
        mv.visitCode();
    }


    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

        mv.visitLabel(TRY_END);
        mv.visitLabel(HANDLER);
        mv.visitInsn(DUP);
        mv.visitInvokeDynamicInsn(
                handleMethodName,
                handleMethodDescriptor,
                new Handle(
                        Opcodes.H_INVOKESTATIC,
                        Type.getInternalName(ExceptionMonitoringBoostrap.class),
                        ExceptionMonitoringBoostrap.BOOTSTRAP_METHOD_NAME,
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
