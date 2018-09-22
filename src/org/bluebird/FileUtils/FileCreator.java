package org.bluebird.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileCreator {

    private static StringBuffer vxlFile = new StringBuffer();
    private static StringBuffer graphDotFile = new StringBuffer();
    private static StringBuffer graphTxtFile = new StringBuffer();
    private static StringBuffer vocabularyTxtFile = new StringBuffer();

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

        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
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
     * Adiciona a string ao vxl
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToVxlFile(String content) {
        FileCreator.vxlFile.append(content);
    }

    /**
     * Adiciona a string ao dot do grafo
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToGraphDotFile(String content) {
        FileCreator.graphDotFile.append(content);
    }

    /**
     * Adiciona a string ao txt do grafo
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToGraphTxtFile(String content) {
        FileCreator.graphTxtFile.append(content);
    }

    /**
     * Adiciona a string ao txt do vocabulario
     *
     * @param content Conteudo para adicionar
     */
    public static void appendToVocabularyTxtFile(String content) {
        FileCreator.vocabularyTxtFile.append(content);
    }
}
