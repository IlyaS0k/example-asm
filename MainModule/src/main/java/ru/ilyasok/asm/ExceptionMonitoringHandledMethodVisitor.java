package ru.ilyasok.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringHandledMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final String handlerClassName;
    private final String handlerFieldName;
    private final String ownerClassName;
    private final String handlerMethodName;
    private final String handlerFieldDescriptor;
    private final String handlerMethodDescriptor;


    protected ExceptionMonitoringHandledMethodVisitor(int api,
                                                      MethodVisitor mv,
                                                      String handlerClassName,
                                                      String handlerFieldName,
                                                      String ownerClassName,
                                                      String handlerMethodName,
                                                      String handlerFieldDescriptor,
                                                      String handlerMethodDescriptor) {
        super(api, mv);
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
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
                GETFIELD,
                ownerClassName,
                handlerFieldName,
                handlerFieldDescriptor
        );
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(
                INVOKEINTERFACE,
                handlerClassName,
                handlerMethodName,
                handlerMethodDescriptor,
                true
        );
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }
}
