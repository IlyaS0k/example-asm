package ru.ilyasok.asm;

import ru.ilyasok.bytecode.Bytecode;


public interface IBytecodeTryCatchWrapper {
    <EXCEPTION_TYPE extends Throwable> Bytecode wrap(Bytecode bytecodeToBeEdit,
                                                     ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                     String className,
                                                     String methodName,
                                                     String methodDescriptor);

}
