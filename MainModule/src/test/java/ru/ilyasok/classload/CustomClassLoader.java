package ru.ilyasok.classload;


import ru.ilyasok.bytecode.Bytecode;

public class CustomClassLoader extends ClassLoader {
    public Class<?> findClass(String className, Bytecode bytecode) {
        String internalName = className.replace(".", "/");
        Class<?> loadedClass = findLoadedClass(internalName);
        if (loadedClass != null) {
            return loadedClass;
        }
        return defineClass(className, bytecode.asBytes(), 0, bytecode.length());
    }


}
