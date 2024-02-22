package ru.ilyasok.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import ru.ilyasok.listener.SimpleListener;

import static org.objectweb.asm.Opcodes.*;
import static ru.ilyasok.asm.RuntimeExceptionMonitoringClassVisitor.*;

public class RuntimeExceptionMonitoringInitMethodListener extends MethodVisitor {
    protected RuntimeExceptionMonitoringInitMethodListener(MethodVisitor mv) {
        super(ASM9, mv);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type.getInternalName(SimpleListener.class),
                "getInstance",
                "()L" + Type.getInternalName(SimpleListener.class) + ";",
                false
        );
        mv.visitFieldInsn(
                PUTSTATIC,
                CLASS_NAME,
                LISTENER_FIELD_NAME,
                LISTENER_FIELD_TYPE
        );
    }


}
