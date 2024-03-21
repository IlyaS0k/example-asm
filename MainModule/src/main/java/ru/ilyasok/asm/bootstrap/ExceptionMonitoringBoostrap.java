package ru.ilyasok.asm.bootstrap;

import java.lang.invoke.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMonitoringBoostrap {

    public static final String BOOTSTRAP_METHOD_NAME = "bootstrap";

    private static final ConcurrentHashMap<EditMethodCoords, MethodHandle> mhs = new ConcurrentHashMap<>();

    public static void setMH(
            MethodHandle methodHandle,
            ClassLoader classLoader,
            String className,
            String methodName,
            String methodDescriptor
    ) {
        EditMethodCoords coords = new EditMethodCoords(classLoader, className, methodName, methodDescriptor);
        if (!mhs.containsKey(coords)) {
            mhs.putIfAbsent(coords, methodHandle);
            return;
        }
        mhs.put(coords, methodHandle);
    }

    public static CallSite bootstrap(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type,
            String wrappedMethodName,
            String wrappedMethodDescriptor
    ) {
        Class<?> lookupClass = lookup.lookupClass();
        EditMethodCoords coords = new EditMethodCoords(
                lookupClass.getClassLoader(),
                lookupClass.getName(),
                wrappedMethodName,
                null
        );
        if (mhs.containsKey(coords)) {
            return new ConstantCallSite(mhs.get(coords));
        }
        coords.methodDescriptor = wrappedMethodDescriptor;
        return new ConstantCallSite(mhs.get(coords));
    }

    private static class EditMethodCoords {

        public ClassLoader classLoader;

        public String className;

        public  String methodName;

        public String methodDescriptor;

        public EditMethodCoords(ClassLoader classLoader, String className, String methodName, String methodDescriptor) {

            this.classLoader = classLoader;

            this.className = className;

            this.methodName = methodName;

            this.methodDescriptor = methodDescriptor == null ? "" : methodDescriptor;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof EditMethodCoords) {
                return Objects.equals(classLoader, ((EditMethodCoords) obj).classLoader) &&
                        Objects.equals(className, ((EditMethodCoords) obj).className) &&
                        Objects.equals(methodName, ((EditMethodCoords) obj).methodName) &&
                        Objects.equals(methodDescriptor, ((EditMethodCoords) obj).methodDescriptor);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return classLoader.hashCode() / 4 + className.hashCode() / 4
                    + methodName.hashCode() / 4 + methodDescriptor.hashCode() / 4;
        }
    }
}