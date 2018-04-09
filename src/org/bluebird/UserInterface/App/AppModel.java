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

    String chooseDir(Button dirBrowser) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Directory");
        File file = chooser.showDialog(dirBrowser.getScene().getWindow());

        return (file != null) ? file.getAbsolutePath() : "";
    }

    boolean languageOptionIsEmpty(ComboBox languages) {
        return languages.getSelectionModel().isEmpty();
    }

    boolean ProjectNameIsEmpty(String name) {
        return name.isEmpty();
    }

    boolean ProjectRevisionIsEmpty(String revision) {
        return revision.isEmpty();
    }

    boolean projectPathIsEmpty(String projectPath) {
        return projectPath.isEmpty();
    }

    boolean vxlPathIsEmpty(String vxlPath) {
        return vxlPath.isEmpty();
    }

    private LanguageWalker returnWalkerObject(String language) {
        try {
            Class walker = Class.forName("org.bluebird.Extractor."+ language + "." + language + "Walker");
            return (LanguageWalker) walker.newInstance();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    void extractVocabulary(String language, String projectName, String revision, String path, String vxlPath) {
        LanguageWalker walker = returnWalkerObject(language);

        if (walker == null) {
            System.exit(1);
        }else {
            FileCreator.appendToXmlFile("<csharp-project name=\"" + projectName + "\" revision=\"" + revision + "\">" + "\n");

            try {
                FileBrowser.browseDirectory(walker, new File(path));
            } catch (IOException error) {
                System.out.println("Path not found");
            }

            FileCreator.appendToXmlFile("</csharp-project>");
            FileCreator.saveFile(projectName, vxlPath, FileCreator.getVxlFile(), "xml");
            if(AppController.getCallGraphCheck()) {
                CallGraph.toTxt();
                FileCreator.saveFile(projectName, vxlPath, FileCreator.getTxtFile(), "txt");
                CallGraph.toDOT();
                FileCreator.saveFile(projectName, vxlPath, FileCreator.getGraphDotFile(), "dot");
            }
        }
    }
}

