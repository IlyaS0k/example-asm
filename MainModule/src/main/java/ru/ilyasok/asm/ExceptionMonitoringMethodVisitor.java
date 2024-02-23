package ru.ilyasok.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ExceptionMonitoringMethodVisitor<EXCEPTION_TYPE extends Throwable>
        extends MethodVisitor {
    private static final String handlerFieldName = "handler$";
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final String className;
    private final String methodName;
    protected ExceptionMonitoringMethodVisitor(int api,
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
    private final Label TRY_START = new Label();
    private final Label TRY_END = new Label();
    private final Label CATCH_START = new Label();

    @Override
    public void visitCode() {

        String handledExceptionType = Type.getInternalName(RuntimeException.class);
        mv.visitTryCatchBlock(TRY_START, TRY_END, CATCH_START, handledExceptionType);
        mv.visitLabel(TRY_START);
        mv.visitCode();
    }


    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

        mv.visitLabel(TRY_END);
        mv.visitLabel(CATCH_START);
        mv.visitInsn(DUP);
        mv.visitFieldInsn(
                GETSTATIC,
                className,
                handlerFieldName,
                Type.getDescriptor(handler.getClass()));
//        mv.visitInsn(SWAP);
//        mv.visitMethodInsn(
//                INVOKEVIRTUAL,
//                Type.getInternalName(Exception.class),
//                "getStackTrace",
//                "()[Ljava/lang/StackTraceElement;",
//                false);
//        mv.visitMethodInsn(
//                INVOKESTATIC,
//                Type.getInternalName(Arrays.class),
//                "toString",
//                "([Ljava/lang/Object;)Ljava/lang/String;",
//                false);
        mv.visitMethodInsn(
                INVOKEINTERFACE,
                Type.getDescriptor(handler.getClass()),
                "handle",
                "(Ljava/lang/RuntimeException;)V",
                true);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }
}
