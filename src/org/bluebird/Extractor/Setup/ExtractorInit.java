package org.bluebird.Extractor.Setup;

import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.FileUtils.FileBrowser;
import org.bluebird.FileUtils.FileCreator;

import java.io.File;
import java.io.IOException;

public class ExtractorInit {

    public ExtractorInit() {
    }

    /**
     * Procurar pelo walker que deve ser utilizado
     * @param language Linguagem do Walker
     * @return Walker da Linguagem
     */
    private LanguageWalker returnWalkerObject(String language) {
        try {
            Class walker = Class.forName("org.bluebird.Extractor."+ language + "." + language + "Walker");
            return (LanguageWalker) walker.newInstance();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Extrai o vocabulario do projeto que foi escolhido
     * @param language Linguagem do projeto
     * @param projectName Nome do projeto
     * @param dirPath Caminho do projeto
     * @param fileToSavePath Caminho para salvar vxl
     */
    public void extractVocabulary(String language, String projectName, String dirPath, String fileToSavePath, String revision) {
        LanguageWalker walker = returnWalkerObject(language);

        if (walker == null) {
            System.exit(1);
        }else {
            if(ExtractorOptions.isVxlEnabled()) {
                FileCreator.appendToVxlFile("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<"+ language.toLowerCase() +"-project name=\"" + projectName + "\" revision=\"" + revision + "\">\n");
            }

            try {
                FileBrowser.browseDirectory(walker, new File(dirPath));
            } catch (IOException error) {
                System.out.println("Path not found");
            }

            if(ExtractorOptions.isVxlEnabled()) {
                FileCreator.appendToVxlFile("</" + language.toLowerCase() + "-project>");
                FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getVxlFile(), "vxl");
            }

            if (ExtractorOptions.isCallGraphEnabled()) {
                CallGraph.toTxt();
                FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getGraphTxtFile(), "txt");
                CallGraph.toDOT();
                FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getGraphDotFile(), "dot");
            }

            if(ExtractorOptions.isVocabularyTxtEnabled()) {
                FileCreator.saveFile(projectName + "Vocabulary", fileToSavePath, FileCreator.getVocabularyTxtFile(), "txt");
            }
        }
    }
}
