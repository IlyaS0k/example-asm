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
            ITryCatchHandler<EXCEPTION_TYPE> handler,
            Class<EXCEPTION_TYPE> exceptionTypeClass,
            String methodToBeWrappedName,
            String methodToBeWrappedDescriptor
    ) {
        System.out.println(className);
        String name = className.replace(".", "\\");
        byte[] array;
        try {
            array = Files.readAllBytes(Paths.get("C:\\Users\\xiao\\IdeaProjects\\exampleASM\\ModuleImpl\\target\\classes\\" + name + ".class"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
        Class<?> loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        String internalName = className.replace(".", "/");
        Bytecode wrappedBytecode = wrapper.wrap(
                new Bytecode(array),
                handler,
                exceptionTypeClass,
                internalName,
                methodToBeWrappedName,
                methodToBeWrappedDescriptor
                );
        byte[] wrapped = wrappedBytecode.asBytes();
        return defineClass(className, wrapped, 0, wrapped.length);
    }




}
