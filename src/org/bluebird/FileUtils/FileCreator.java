package org.bluebird.FileUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileCreator {

    private static StringBuffer xmlFile = new StringBuffer();

    public static void saveXmlFile(String fileName, String path) {
        FileWriter fileWriter;
        BufferedWriter writer;

        try {
            fileWriter = new FileWriter(path + "/" + fileName + ".xml");
            writer = new BufferedWriter(fileWriter);

            writer.append(xmlFile);

            writer.flush();
            xmlFile.setLength(0);
            writer.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Não foi possivel abrir arquivo");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problemas com leitura do arquivo");
        }
    }

    public static void appendToFile(String content) {
        xmlFile.append(content);
    }
}
