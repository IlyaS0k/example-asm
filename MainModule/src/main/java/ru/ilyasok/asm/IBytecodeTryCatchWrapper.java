package ru.ilyasok.asm;

import ru.ilyasok.bytecode.Bytecode;


public interface IBytecodeTryCatchWrapper {
    <EXCEPTION_TYPE extends Throwable> Bytecode wrap(Bytecode bytecodeToBeEdit,
                                                     String className,
                                                     ClassLoader classLoader,
                                                     Class<EXCEPTION_TYPE> exceptionClass,
                                                     ITryCatchHandler<EXCEPTION_TYPE> handler,
                                                     String methodName,
                                                     String methodDescriptor);

}
