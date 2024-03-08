package ru.ilyasok.asm.bootstrap;

import java.lang.invoke.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMonitoringBoostrap {

    public static final String BOOTSTRAP_METHOD_NAME = "bootstrap";

    private static final ConcurrentHashMap<ClassCoords, MethodHandle> mhs = new ConcurrentHashMap<>();

    public static void setMH(
            MethodHandle methodHandle,
            ClassLoader classLoader,
            String className
    ) {
        ClassCoords coords = new ClassCoords(classLoader, className);
        if (!mhs.containsKey(coords)) {
            mhs.putIfAbsent(coords, methodHandle);
            return;
        }
        mhs.put(coords, methodHandle);
    }

    public static CallSite bootstrap(MethodHandles.Lookup lookup,
                                     String name,
                                     MethodType type) {
        Class<?> lookupClass = lookup.lookupClass();
        ClassCoords coords = new ClassCoords(lookupClass.getClassLoader(), lookupClass.getName());
        return new ConstantCallSite(mhs.get(coords));
    }

    private static class ClassCoords {

        public ClassLoader classLoader;

        public String className;

        public ClassCoords(ClassLoader classLoader, String className) {

            this.classLoader = classLoader;

            this.className = className;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClassCoords) {
                return Objects.equals(classLoader, ((ClassCoords) obj).classLoader) &&
                        Objects.equals(className, ((ClassCoords) obj).className);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return classLoader.hashCode() / 2 + className.hashCode() / 2;
        }
    }
}
