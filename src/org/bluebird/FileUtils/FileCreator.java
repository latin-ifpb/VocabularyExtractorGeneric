package org.bluebird.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileCreator {

    private static StringBuffer vxlFile = new StringBuffer();
    private static StringBuffer graphDotFile = new StringBuffer();

    public static StringBuffer getGraphDotFile() {
        return graphDotFile;
    }

    public static StringBuffer getVxlFile() {
        return vxlFile;
    }

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

    public static void appendToXmlFile(String content) {
        vxlFile.append(content);
    }

    public static void appendToDotFile(String content) {
        graphDotFile.append(content);
    }
}
