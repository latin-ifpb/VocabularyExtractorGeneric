package org.bluebird.Utils;

public class MemoryRuntime {

    /**
     * Calcula memória utilizada pelo extrator
     */
    public void calculateAll() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memoria utilizada em bytes: " + memory);
        System.out.println("Memoria utilizada em megabytes: " + bytesToMegabytes(memory));
        System.out.println("Memoria utilizada em Kbytes: " + bytesToKbytes(memory));

        memory = runtime.maxMemory();
        System.out.println("Máximo de memória utilizada em Kbytes: " + bytesToKbytes(memory));

        long processor = runtime.availableProcessors();
        System.out.println("Número de processadores ativos: " + processor);
    }

    /**
     * Converte bytes para megabytes
     *
     * @param bytes valor em bytes
     * @return valor convertido em megabytes
     */
    private long bytesToMegabytes(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    /**
     * Converte bytes para kilobytes
     *
     * @param bytes valor em bytes
     * @return valor convertido em kilobytes
     */
    private long bytesToKbytes(long bytes) {
        final long KBYTE = 1024L;
        return bytes / KBYTE;
    }

}