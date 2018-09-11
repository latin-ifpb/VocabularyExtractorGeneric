package org.bluebird.Extractor.Setup;

import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.FileUtils.FileBrowser;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.Utils.MemoryRuntime;

import java.io.File;
import java.io.IOException;

public class ExtractorInit {

    private MemoryRuntime memoryRuntime;

    public ExtractorInit() {
    }

    /**
     * Procurar pelo walker que deve ser utilizado
     *
     * @param language Linguagem do Walker
     * @return Walker da Linguagem
     */
    private LanguageWalker returnWalkerObject(String language) {
        try {
            Class walker = Class.forName("org.bluebird.Extractor.Languages." + language + "." + language + "Walker");
            return (LanguageWalker) walker.newInstance();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Extrai o vocabulario do projeto que foi escolhido
     *
     * @param languageOption Linguagens do projeto
     * @param projectName    Nome do projeto
     * @param dirPath        Caminho do projeto
     * @param fileToSavePath Caminho para salvar vxl
     */
    public void extractVocabulary(String[] languageOption, String projectName, String dirPath, String fileToSavePath, String revision) {
        long start = System.currentTimeMillis(), elapsedTimeMillis;
        float elapsedTimeSec;

        if(ExtractorOptions.isMemoryRuntimeEnabled()) {
            memoryRuntime = new MemoryRuntime();
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<project name=\"" + projectName + "\" revision=\"" + revision + "\">\n");

        }

        for (String language : languageOption) {
            LanguageWalker walker = returnWalkerObject(language);

            if (walker == null) {
                System.exit(1);
            } else {
                if (ExtractorOptions.isVxlEnabled()) {
                    FileCreator.appendToVxlFile("<" + language.toLowerCase() + "-project>\n");
                }

                try {
                    FileBrowser.browseDirectory(walker, new File(dirPath));
                } catch (IOException error) {
                    System.out.println("Path not found");
                }

                if (ExtractorOptions.isVxlEnabled()) {
                    FileCreator.appendToVxlFile("</" + language.toLowerCase() + "-project>\n");
                }
            }
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("</project>");
            FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getVxlFile(), "vxl");
        }

        if (ExtractorOptions.isCallGraphEnabled()) {
            CallGraph.toTxt();
            FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getGraphTxtFile(), "txt");
            CallGraph.toDOT();
            FileCreator.saveFile(projectName, fileToSavePath, FileCreator.getGraphDotFile(), "dot");
        }

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.saveFile(projectName + "Vocabulary", fileToSavePath, FileCreator.getVocabularyTxtFile(), "txt");
        }

        if (ExtractorOptions.isMemoryRuntimeEnabled()) {
            memoryRuntime.calculateAll();
            elapsedTimeMillis = System.currentTimeMillis() - start;
            elapsedTimeSec = elapsedTimeMillis/1000;
            System.out.println("Tempo em Milisegundos: " + elapsedTimeMillis + "\nTempo em Segundos: " + elapsedTimeSec);
        }
    }
}
