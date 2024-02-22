package ru.ilyasok.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import ru.ilyasok.listener.IListener;


import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;
import static ru.ilyasok.asm.RuntimeExceptionMonitoringClassVisitor.*;

public class RuntimeExceptionMonitoringInvokeMethodVisitor extends MethodVisitor {
    protected RuntimeExceptionMonitoringInvokeMethodVisitor(MethodVisitor mv) {
        super(ASM9, mv);
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
                CLASS_NAME,
                LISTENER_FIELD_NAME,
                LISTENER_FIELD_TYPE);
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(
                INVOKEVIRTUAL,
                Type.getInternalName(Exception.class),
                "getStackTrace",
                "()[Ljava/lang/StackTraceElement;",
                false);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type.getInternalName(Arrays.class),
                "toString",
                "([Ljava/lang/Object;)Ljava/lang/String;",
                false);
        mv.visitMethodInsn(
                INVOKEINTERFACE,
                Type.getInternalName(IListener.class),
                "notifyService",
                "(Ljava/lang/String;)V",
                true);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }
}
