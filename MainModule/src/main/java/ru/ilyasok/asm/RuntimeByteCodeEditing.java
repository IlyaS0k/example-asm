package ru.ilyasok.asm;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class RuntimeByteCodeEditing {

    public byte[] getEditedBytecode(String path) throws IOException {
        byte[] b = getByteCode(path);
        ClassReader cr = new ClassReader(b);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new RuntimeExceptionMonitoringClassVisitor(cw);

        cr.accept(cv, ClassReader.SKIP_FRAMES);

        return cw.toByteArray();
    }

    private static byte[] getByteCode(String path) throws IOException {
        File f = new File(path);
        byte[] buff = new byte[(int)f.length()];
        try (InputStream is = Files.newInputStream(f.toPath())) {
            int offset = 0;
            int numRead = 0;
            while (offset < buff.length
                    && (numRead = is.read(buff, offset, buff.length - offset)) >= 0) {
                offset += numRead;
            }
        }
        return buff;
    }

}
