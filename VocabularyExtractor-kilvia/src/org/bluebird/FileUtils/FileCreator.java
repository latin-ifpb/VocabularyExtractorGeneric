package org.bluebird.FileUtils;

import java.io.*;

public class FileCreator {

    private static StringBuffer vxlFile = new StringBuffer();
    private static StringBuffer graphDotFile = new StringBuffer();
    private static StringBuffer graphTxtFile = new StringBuffer();
    private static StringBuffer vocabularyTxtFile = new StringBuffer();
    private static StringBuffer memoryRuntimeFile = new StringBuffer();

    /**
     * Retorna o string da perfomance do extrator
     *
     * @return String da perfomance do extrator
     */
    public static StringBuffer getMemoryRuntimeFile() {
        return FileCreator.memoryRuntimeFile;
    }

    /**
     * Retorna o string do vocabulario do txt
     *
     * @return String do txt do vocabulario
     */
    public static StringBuffer getVocabularyTxtFile() {
        return FileCreator.vocabularyTxtFile;
    }

    /**
     * Retorna o string do grafo
     *
     * @return String do grafo
     */
    public static StringBuffer getGraphDotFile() {
        return FileCreator.graphDotFile;
    }

    /**
     * Retorna o string do vxl
     *
     * @return String do vxl
     */
    public static StringBuffer getVxlFile() {
        return FileCreator.vxlFile;
    }

    /**
     * Retorna o string da analise do grafo
     *
     * @return String do txt do grafo
     */
    public static StringBuffer getGraphTxtFile() {
        return FileCreator.graphTxtFile;
    }

    /**
     * Salva um arquivo
     *
     * @param fileName Nome do arquivo
     * @param path     Caminho do arquivo
     * @param type     String do arquivo para processar
     * @param format   Formato do arquivo
     */
    public static void saveFile(String fileName, String path, StringBuffer type, String format) {
        FileWriter fileWriter;
        BufferedWriter writer;
        File directory;

        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        directory = new File(path + "/");
        if (!directory.exists()){
            directory.mkdirs();
        }

        try {
            fileWriter = new FileWriter(path + "/" + fileName + "." + format);
            writer = new BufferedWriter(fileWriter);

            writer.append(type);

            writer.flush();
            type.setLength(0);
            writer.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possivel abrir arquivo");
        } catch (IOException e) {
            System.out.println("Problemas com leitura do arquivo");
        }
    }

    /**
     * Adiciona a string ao StringBuffer do vxl (vxl)
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToVxlFile(String content) {
        FileCreator.vxlFile.append(content);
    }

    /**
     * Adiciona a string ao StringBuffer do grafo (dot)
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToGraphDotFile(String content) {
        FileCreator.graphDotFile.append(content);
    }

    /**
     * Adiciona a string ao StringBuffer do grafo (txt)
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToGraphTxtFile(String content) {
        FileCreator.graphTxtFile.append(content);
    }

    /**
     * Adiciona a string ao StringBuffer do vocabulario (txt)
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToVocabularyTxtFile(String content) {
        FileCreator.vocabularyTxtFile.append(content);
    }

    /**
     * Adiciona a string ao StringBuffer do stats (txt)
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToMemoryRuntimeFile(String content) {
        FileCreator.memoryRuntimeFile.append(content);
    }
}
