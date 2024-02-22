package ru.ilyasok.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class RuntimeExceptionMonitoringClassVisitor extends ClassVisitor {

    public static final String CLASS_NAME = "ru/ilyasok/RunTimeLoadModule";
    public static final String LISTENER_FIELD_NAME = "listener$";
    public static final String LISTENER_FIELD_TYPE = "Lru/ilyasok/listener/IListener;";

    protected RuntimeExceptionMonitoringClassVisitor(ClassVisitor cv) {
        super(ASM9, cv);

    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && name.startsWith("invoke")) {
            return new RuntimeExceptionMonitoringInvokeMethodVisitor(mv);
        }

        if (mv != null && "<init>".equals(name)) {
            return new RuntimeExceptionMonitoringInitMethodListener(mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        FieldVisitor fv =  cv.visitField(
                ACC_PRIVATE | ACC_FINAL | ACC_STATIC,
                LISTENER_FIELD_NAME,
                LISTENER_FIELD_TYPE,
                null,
                null);
        if (fv != null) {
            fv.visitEnd();
        }
        cv.visitEnd();
    }
}
