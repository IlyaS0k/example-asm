package ru.ilyasok.bytecode;

public class Bytecode {
    private final byte[] bytecode;

    public Bytecode(byte[] bytecode) {
        this.bytecode = bytecode.clone();
    }

    public byte[] asBytes() {
        return bytecode.clone();
    }

    public int length() {
        return bytecode.length;
    }
}
