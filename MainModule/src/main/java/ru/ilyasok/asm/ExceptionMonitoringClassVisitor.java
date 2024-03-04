package ru.ilyasok.asm;

import org.objectweb.asm.*;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {
    private static final String handlerFieldNamePrefix = "handler$exception$monitor$";
    private static final String lambdaMethodNamePrefix = "lambda$exception$monitor$";
    private static final String lookupInternalName = "java/lang/invoke/MethodHandles$Lookup";
    private static final String lookupOuterName = "java/lang/invoke/MethodHandles";
    private static final String lookupInnerName = "Lookup";
    private static final String handlerMethodName = "handle";
    private static final int lookupAccessMask = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC;
    private final String lambdaMethodName;
    private final String handlerFieldName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final String handlerClassName;
    private final Class<?> handledExceptionClass;
    private final String ownerClassName;
    private final String wrappedMethodName;
    private final String wrappedMethodDescriptor;
    private final String handlerFieldSignature;
    private final String handlerFieldDescriptor;
    private final String handlerMethodDescriptor;
    private boolean containsLookup = false;

    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           Class<EXCEPTION_TYPE> handledExceptionClass,
                                           String ownerClassName,
                                           String wrappedMethodName,
                                           String wrappedMethodDescriptor) {
        super(api, cv);
        this.handler = handler;
        this.wrappedMethodName = wrappedMethodName;
        this.wrappedMethodDescriptor = wrappedMethodDescriptor;
        this.ownerClassName = ownerClassName;
        this.handledExceptionClass = handledExceptionClass;
        this.handlerClassName = Type.getInternalName(ITryCatchHandler.class);
        String notNullMethodDescriptor = wrappedMethodDescriptor == null ? "" : wrappedMethodDescriptor;
        this.lambdaMethodName = lambdaMethodNamePrefix + wrappedMethodName + "$" + notNullMethodDescriptor;
        this.handlerFieldName = handlerFieldNamePrefix + wrappedMethodName + "$" + notNullMethodDescriptor;
        this.handlerFieldDescriptor = Type.getDescriptor(ITryCatchHandler.class);
        this.handlerFieldSignature = handlerFieldDescriptor.substring(0, handlerFieldDescriptor.length() - 1) +
                "<" + Type.getDescriptor(handledExceptionClass) + ">;";
        this.handlerMethodDescriptor = "(" + Type.getDescriptor(handledExceptionClass) + ")V";
    }

    @Override
    public void visitInnerClass(String name,
                                String outerName,
                                String innerName,
                                int access) {
          if (name.equals(lookupInternalName) &&
                  outerName.equals(lookupOuterName) &&
                  innerName.equals(lookupInnerName) &&
                  access == lookupAccessMask
          ) {
              containsLookup = true;
          }
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null &&
                ((wrappedMethodDescriptor == null && name.equals(wrappedMethodName)) ||
                        (name.equals(wrappedMethodName) && desc.equals(wrappedMethodDescriptor)))
        ) {
            return new ExceptionMonitoringHandledMethodVisitor<>(
                    api,
                    mv,
                    handlerClassName,
                    handlerFieldName,
                    ownerClassName,
                    handlerMethodName,
                    handlerFieldDescriptor,
                    handlerMethodDescriptor
            );
        }

        if (mv != null && "<init>".equals(name)) {
            return new ExceptionMonitoringInitMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handledExceptionClass,
                    handlerFieldName,
                    handlerFieldDescriptor,
                    ownerClassName,
                    handlerMethodName,
                    wrappedMethodName,
                    wrappedMethodDescriptor,
                    lambdaMethodName
            );
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        FieldVisitor fv = cv.visitField(
                Opcodes.ACC_PRIVATE,
                handlerFieldName,
                handlerFieldDescriptor,
                handlerFieldSignature,
                null
        );
        if (fv != null) {
            fv.visitEnd();
        }
        if (!containsLookup) {
            cv.visitInnerClass(
                    lookupInternalName,
                    lookupOuterName,
                    lookupInnerName,
                    lookupAccessMask
            );
        }
        cv.visitEnd();
    }
}
