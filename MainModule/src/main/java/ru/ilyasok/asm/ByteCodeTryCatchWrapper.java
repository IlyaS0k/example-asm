package ru.ilyasok.asm;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import ru.ilyasok.bytecode.Bytecode;

import static org.objectweb.asm.Opcodes.ASM9;

public class ByteCodeTryCatchWrapper
        implements IBytecodeTryCatchWrapper {

    private static final int API = ASM9;
    @Override
    public <EXCEPTION_TYPE extends Throwable> Bytecode wrap(Bytecode bytecodeToBeEdit,
                                               ITryCatchHandler<EXCEPTION_TYPE> handler,
                                               String className,
                                               String methodName,
                                               String methodDescriptor) {
        ClassReader cr = new ClassReader(bytecodeToBeEdit.asBytes());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ExceptionMonitoringClassVisitor<>(
                API,
                cw,
                handler,
                className,
                methodName,
                methodDescriptor
        );
        cr.accept(cv, ClassReader.SKIP_FRAMES);
        return new Bytecode(cw.toByteArray());
    }


}
