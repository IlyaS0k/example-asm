package ru.ilyasok.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringInitMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final String handlerFieldName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final String className;


    protected ExceptionMonitoringInitMethodVisitor(int api,
                                                   MethodVisitor mv,
                                                   ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                   Class<?> handledExceptionClass,
                                                   String handlerFieldName,
                                                   String className

    ) {
        super(api, mv);
        this.handler = handler;
        this.className = className;
        this.handledExceptionClass = handledExceptionClass;
        this.handlerFieldName = handlerFieldName;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type.getInternalName(handledExceptionClass),
                "getInstance",
                "()L;",
                false
        );
        mv.visitFieldInsn(
                PUTFIELD,
                className,
                handlerFieldName,
                Type.getDescriptor(handledExceptionClass)
        );
    }


}
