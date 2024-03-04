package ru.ilyasok.classload;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomClassLoader extends ClassLoader {
    public Class<?> findClass(String className) {
        System.out.println(className);
        String name = className.replace(".", "\\");
        byte[] array;
        try {
            array = Files.readAllBytes(Paths.get("C:\\Users\\xiao\\IdeaProjects\\exampleASM\\ModuleImpl\\target\\classes\\" + name + ".class"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
        String internalName = className.replace("\\", "/");
        Class<?> loadedClass = findLoadedClass(internalName);
        if (loadedClass != null) {
            return loadedClass;
        }
        return defineClass(className, array, 0, array.length);
    }
}
