package org.bluebird.UserInterface.App;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.DirectoryChooser;
import org.bluebird.Extractor.FileBrowser;
import org.bluebird.Extractor.LanguageWalker;

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
            e.printStackTrace();
        }
        return null;
    }

    void extractVocabulary(String language, String projectName, String revision, String path) {
        LanguageWalker walker = returnWalkerObject(language);

        if (walker == null) {
            System.exit(1);
        }else {
            try {
                FileBrowser.browseDirectory(walker, new File(path));
            } catch (IOException error) {
                System.out.println("Path not found");
            }
        }
    }
}
