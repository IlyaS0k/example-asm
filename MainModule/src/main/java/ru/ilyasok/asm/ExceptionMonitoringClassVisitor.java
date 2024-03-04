package ru.ilyasok.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class ExceptionMonitoringClassVisitor<EXCEPTION_TYPE extends Throwable>
        extends ClassVisitor {

    private static final String initMethodName = "<init>";
    private static final String handlerFieldName = "handler$excptnmonitor$";
    private static final String lambdaMethodNamePrefix = "lambda$excptnmonitor$";
    private static final String lookupInternalName = "java/lang/invoke/MethodHandles$Lookup";
    private static final String lookupOuterName = "java/lang/invoke/MethodHandles";
    private static final String lookupInnerName = "Lookup";
    int lookupAccessMask = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC;
    private final String lambdaMethodName;
    private final ITryCatchHandler<EXCEPTION_TYPE> handler;
    private final Class<?> handledExceptionClass;
    private final Class<?> handlerClass;
    private final Method handlerMethod;
    private final String className;
    private final String methodName;
    private final String methodDescriptor;
    private boolean containsLookup = false;

    @SuppressWarnings("unchecked")
    public ExceptionMonitoringClassVisitor(int api,
                                           ClassVisitor cv,
                                           ITryCatchHandler<EXCEPTION_TYPE> handler,
                                           String className,
                                           String methodName,
                                           String methodDescriptor) {
        super(api, cv);
        this.handler = handler;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.className = className;
        this.handledExceptionClass = (Class<EXCEPTION_TYPE>)
                ((ParameterizedType) this.getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        this.handlerClass = handler.getClass();
        this.handlerMethod = Arrays.stream(handler.getClass().getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().orElseThrow();
        String descriptorAsStr = methodDescriptor == null ? "" : methodDescriptor;
        this.lambdaMethodName = lambdaMethodNamePrefix + methodName + "$" + descriptorAsStr;
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
                ((methodDescriptor == null && name.equals(methodName)) ||
                        (name.equals(methodName) && desc.equals(methodDescriptor)))
        ) {
            return new ExceptionMonitoringHandledMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handlerClass,
                    handlerFieldName,
                    className,
                    handlerMethod);
        }

        if (mv != null && initMethodName.equals(name)) {
            return new ExceptionMonitoringInitMethodVisitor<>(
                    api,
                    mv,
                    handler,
                    handledExceptionClass,
                    handlerFieldName,
                    className,
                    handlerMethod,
                    methodName,
                    methodDescriptor,
                    lambdaMethodName);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!containsLookup) {
            cv.visitInnerClass(
                    lookupInternalName,
                    lookupOuterName,
                    lookupInnerName,
                    lookupAccessMask
            );
        }
    }
}
