package org.bluebird.UserInterface.App;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.DirectoryChooser;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.FileUtils.FileBrowser;
import org.bluebird.FileUtils.FileCreator;

import java.io.File;
import java.io.IOException;

class AppModel {

    /**
     * Escolhe diretorio para pecorrer
     * @param dirBrowser Botao da interface
     * @return Caminho do diretorio
     */
    String chooseDir(Button dirBrowser) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Directory");
        File file = chooser.showDialog(dirBrowser.getScene().getWindow());

        return (file != null) ? file.getAbsolutePath() : "";
    }

    /**
     * Verifica se alguma linguagem foi selecionada
     * @param languages Opcoes de linguagens
     * @return Se ta vazio ou nao
     */
    boolean languageOptionIsEmpty(ComboBox languages) {
        return languages.getSelectionModel().isEmpty();
    }

    /**
     * Verifica se foi digitado nome do projeto
     * @param name Nome do projeto
     * @return Se ta vazio ou nao
     */
    boolean ProjectNameIsEmpty(String name) {
        return name.isEmpty();
    }

    /**
     * Verifica se foi digitado revision do projeto
     * @param revision Revision do projeto
     * @return Se ta vazio ou nao
     */
    boolean ProjectRevisionIsEmpty(String revision) {
        return revision.isEmpty();
    }

    /**
     * Verifica se foi selecionado algum diretorio
     * @param projectPath Caminho do projeto
     * @return Se ta vazio ou nao
     */
    boolean projectPathIsEmpty(String projectPath) {
        return projectPath.isEmpty();
    }

    /**
     * Verifica se foi selecionado caminho do vxl
     * @param vxlPath Caminho do vxl
     * @return Se ta vazio ou nao
     */
    boolean vxlPathIsEmpty(String vxlPath) {
        return vxlPath.isEmpty();
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
     * @param revision Revision do projeto
     * @param path Caminho do projeto
     * @param vxlPath Caminho para salvar vxl
     */
    void extractVocabulary(String language, String projectName, String revision, String path, String vxlPath) {
        LanguageWalker walker = returnWalkerObject(language);

        if (walker == null) {
            System.exit(1);
        }else {
            FileCreator.appendToVxlFile("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            FileCreator.appendToVxlFile("<"+ language.toLowerCase() +"-project name=\"" + projectName + "\" revision=\"" + revision + "\">" + "\n");

            try {
                FileBrowser.browseDirectory(walker, new File(path));
            } catch (IOException error) {
                System.out.println("Path not found");
            }

            FileCreator.appendToVxlFile("</" + language.toLowerCase() + "-project>");
            FileCreator.saveFile(projectName, vxlPath, FileCreator.getVxlFile(), "vxl");
            if(AppController.getCallGraphCheck()) {
                CallGraph.toTxt();
                FileCreator.saveFile(projectName, vxlPath, FileCreator.getTxtFile(), "txt");
                CallGraph.toDOT();
                FileCreator.saveFile(projectName, vxlPath, FileCreator.getGraphDotFile(), "dot");
            }
        }
    }
}

