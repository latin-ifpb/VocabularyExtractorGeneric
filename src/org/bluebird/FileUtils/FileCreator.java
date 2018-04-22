package org.bluebird.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileCreator {

    private static StringBuffer vxlFile = new StringBuffer();
    private static StringBuffer graphDotFile = new StringBuffer();
    private static StringBuffer txtFile = new StringBuffer();

    /**
     * Retorna o string do grafo
     * @return String do grafo
     */
    public static StringBuffer getGraphDotFile() {
        return FileCreator.graphDotFile;
    }

    /**
     * Retorna o string do vxl
     * @return String do vxl
     */
    public static StringBuffer getVxlFile() {
        return FileCreator.vxlFile;
    }

    /**
     * Retorna o string da analise do grafo
     * @return String do txt
     */
    public static StringBuffer getTxtFile() {
        return FileCreator.txtFile;
    }

    /**
     * Salva um arquivo
     * @param fileName Nome do arquivo
     * @param path Caminho do arquivo
     * @param type String do arquivo para processar
     * @param format Formato do arquivo
     */
    public static void saveFile(String fileName, String path, StringBuffer type, String format) {
        FileWriter fileWriter;
        BufferedWriter writer;

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
     * Adiciona a string do vxl
     * @param content Conteudo para adc
     */
    public static void appendToVxlFile(String content) {
        FileCreator.vxlFile.append(content);
    }

    /**
     * Adiciona a string do grafo
     * @param content Conteudo para adc
     */
    public static void appendToDotFile(String content) {
        FileCreator.graphDotFile.append(content);
    }

    /**
     * Adiciona a string do txt
     * @param content Conteudo para adc
     */
    public static void appendToTxtFile(String content) {
        FileCreator.txtFile.append(content);
    }
}
