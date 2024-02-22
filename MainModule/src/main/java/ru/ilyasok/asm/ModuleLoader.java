package ru.ilyasok.asm;


import java.io.IOException;

public class ModuleLoader extends ClassLoader {

    private final RuntimeByteCodeEditing byteCodeEditing;
    public ModuleLoader(RuntimeByteCodeEditing byteCodeEditing) {
        this.byteCodeEditing = byteCodeEditing;
    }

    @Override
    public Class<?> findClass(String module, String className) {
        String internalName = className.replace(".", "/");
        String path = "../" + module + "/target/classes/" + internalName + ".class";
        Class<?> loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        byte[] byteCode = new byte[0];
        try {
            byteCode = byteCodeEditing.getEditedBytecode(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return defineClass(className, byteCode, 0, byteCode.length);

    }


}
