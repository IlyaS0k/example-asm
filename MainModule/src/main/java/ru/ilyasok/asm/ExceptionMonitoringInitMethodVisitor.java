package ru.ilyasok.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringInitMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {

    private final String handlerFieldName = "handler$";
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final String className;
    private final String methodName;
    protected ExceptionMonitoringInitMethodVisitor(int api,
                                                   MethodVisitor mv,
                                                   ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                   String className,
                                                   String methodName

    ) {
        super(api, mv);
        this.handler = handler;
        this.className = className;
        this.methodName = methodName;

    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type.getInternalName(handler.getClass()),
                "getInstance",
                "()L;",
                false
        );
        mv.visitFieldInsn(
                PUTFIELD,
                className,
                handlerFieldName,
                Type.getDescriptor(handler.getClass())
        );
    }


}
