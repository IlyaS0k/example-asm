package ru.ilyasok.classload;


import ru.ilyasok.asm.IBytecodeTryCatchWrapper;
import ru.ilyasok.asm.ITryCatchHandler;
import ru.ilyasok.bytecode.Bytecode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EditBytecodeClassLoader extends ClassLoader {

    private final IBytecodeTryCatchWrapper wrapper;
    public EditBytecodeClassLoader(IBytecodeTryCatchWrapper wrapper) {
        this.wrapper = wrapper;
    }
    public<EXCEPTION_TYPE extends Throwable> Class<?> editClass(
            String className,
            Class<EXCEPTION_TYPE> exceptionClass,
            ITryCatchHandler<EXCEPTION_TYPE> handler,
            String methodToBeWrappedName,
            String methodToBeWrappedDescriptor
    ) {
        System.out.println(className);
        String name = className.replace(".", "/");
        byte[] array;
        try {
            array = Files.readAllBytes(Paths.get("/home/ilyasokolov/projects/exampleASM/ModuleImpl/target/classes/" + name + ".class"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
        Class<?> loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        Bytecode wrappedBytecode = wrapper.wrap(
                new Bytecode(array),
                exceptionClass,
                handler,
                methodToBeWrappedName,
                methodToBeWrappedDescriptor
                );
        byte[] wrapped = wrappedBytecode.asBytes();
        return defineClass(className, wrapped, 0, wrapped.length);
    }




}
