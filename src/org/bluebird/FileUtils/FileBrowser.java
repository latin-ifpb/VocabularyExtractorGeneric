package org.bluebird.FileUtils;

import org.bluebird.Extractor.LanguageWalker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileBrowser {

    private static String actualFile = "";

    /**
     * Retorna o nome do arquivo
     * @return Nome do arquivo
     */
    public static String getActualFile() {
        return FileBrowser.actualFile;
    }

    /**
     * Le um arquivo qualquer
     * @param file Arquivo para ler
     * @param encoding Encoding desejado
     * @return O string do arquivo
     * @throws IOException Erro de leitura de arquivo
     */
    public static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    /**
     * Pecorre o diretorio procurando por arquivos no formato desejado
     * @param walker Walker da linguagem que vai ser utilizado
     * @param directory Diretorio que deseja pecorrer
     * @throws IOException Erro de leitura de arquivo
     */
    public static void browseDirectory(LanguageWalker walker, File directory) throws IOException {
        for (File child : directory.listFiles()) {
            if (child.isDirectory()) {
                browseDirectory(walker, child);
            } else {
                if (child.getName().endsWith("." + walker.languageFormat())) {
                    FileBrowser.actualFile = child.getName();
                    walker.walkFileTree(child);
                }
            }
        }
    }
}
