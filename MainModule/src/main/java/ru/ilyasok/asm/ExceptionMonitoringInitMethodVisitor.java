package ru.ilyasok.asm;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import ru.ilyasok.asm.bootstrap.ExceptionMonitoringBoostrap;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringInitMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final String handlerFieldName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final String ownerClassName;
    private final String handlerMethodName;
    private final String handlerFieldDescriptor;
    private final String handlerMethodDescriptor;
    private final String methodName;
    private final String methodDescriptor;
    private final String lambdaMethodName;

    protected ExceptionMonitoringInitMethodVisitor(int api,
                                                   MethodVisitor mv,
                                                   ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                   Class<?> handledExceptionClass,
                                                   String handlerFieldName,
                                                   String handlerFieldDescriptor,
                                                   String handlerMethodDescriptor,
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
        this.handlerMethodDescriptor = handlerMethodDescriptor;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor == null ? "" : methodDescriptor;
        this.lambdaMethodName = lambdaMethodName;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, ownerClassName, "<init>", "()V", false);
        mv.visitTypeInsn(NEW, Type.getInternalName(handler.getClass()));
        mv.visitInsn(DUP);
        mv.visitMethodInsn(
                INVOKESPECIAL,
                Type.getInternalName(handler.getClass()),
                "<init>",
                "()V",
                false
        );
        mv.visitFieldInsn(
                PUTFIELD,
                ownerClassName,
                handlerFieldName,
                handlerFieldDescriptor
        );
    }
}
