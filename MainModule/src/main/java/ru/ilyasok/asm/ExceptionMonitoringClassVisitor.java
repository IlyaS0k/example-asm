package ru.ilyasok.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {
    private static final String handlerFieldName = "handler$";
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final String className;
    private final String methodName;


    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           String className,
                                           String methodName) {
        super(api, cv);
        this.handler = handler;
        this.methodName = methodName;
        this.className = className;
    }



    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && name.equals(methodName)) {
            return new ExceptionMonitoringMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    className,
                    methodName
            );
        }

        if (mv != null && "<init>".equals(name)) {
            return new ExceptionMonitoringInitMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    className,
                    methodName
            );
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        FieldVisitor fv = cv.visitField(
                ACC_PRIVATE | ACC_FINAL,
                handlerFieldName,
                Type.getDescriptor(handler.getClass()),
                null,
                null);
        if (fv != null) {
            fv.visitEnd();
        }
        cv.visitEnd();
    }
}
