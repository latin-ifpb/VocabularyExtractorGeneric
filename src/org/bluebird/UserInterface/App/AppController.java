package org.bluebird.UserInterface.App;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bluebird.Runner;
import org.bluebird.UserInterface.Utils.SecondaryWindowsInit;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private Runner runner = new Runner();
    private AppModel appModel = new AppModel();

    @FXML
    private ComboBox<languageOption> languageComboBox;

    @FXML
    private TextField projectName;

    @FXML
    private TextField projectRevision;

    @FXML
    private Button dirBrowser;

    @FXML
    private Button vxlBrowser;

    @FXML
    private Label filePath;

    @FXML
    private Label vxlDirPath;

    @FXML
    private Label nameStatus;

    @FXML
    private Label revisionStatus;

    @FXML
    private Label dirPathStatus;

    @FXML
    private Label vxlPathStatus;

    @FXML
    private Label languageStatus;

    private String dirPath = "";
    private String vxlFilePath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.languageComboBox.setItems(FXCollections.observableArrayList(languageOption.values()));
    }

    @FXML
    public void openGitHub() {
        try {
            this.runner.getHostServices().showDocument("https://github.com/witoriamanuely/Projeto_POO");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void browseProjectDirectory() {
        this.dirPath = this.appModel.chooseDir(this.dirBrowser);
        this.filePath.setText(this.dirPath);
    }

    @FXML
    public void vxlLocation() {
        this.vxlFilePath = this.appModel.chooseDir(this.vxlBrowser);
        if (projectName.getText().isEmpty()) {
            this.vxlDirPath.setText(this.vxlFilePath + "/Default.vxl");
        } else {
            this.vxlDirPath.setText(this.vxlFilePath + "/" + projectName.getText() + ".vxl");
        }
    }

    @FXML
    public void extract() {
        String projectName;
        String revisionName;
        String languageOption;

        this.nameStatus.setText("");
        this.revisionStatus.setText("");
        this.dirPathStatus.setText("");
        this.vxlPathStatus.setText("");

        if (appModel.languageOptionIsEmpty(this.languageComboBox)) {
            this.languageStatus.setText("Choose a language");
        } else {
            languageOption = (this.languageComboBox.getValue()).value();
            if (appModel.ProjectNameIsEmpty(this.projectName.getText())) {
                this.nameStatus.setText("Project name is empty");
            } else {
                projectName = this.projectName.getText();
                if (appModel.ProjectRevisionIsEmpty(this.projectRevision.getText())) {
                    this.revisionStatus.setText("Project revision is empty");
                } else {
                    revisionName = this.projectRevision.getText();
                    if (appModel.projectPathIsEmpty(this.dirPath)) {
                        this.dirPathStatus.setText("Path empty");
                    } else if (appModel.vxlPathIsEmpty(this.vxlFilePath)) {
                        this.vxlPathStatus.setText("Path empty");
                    } else {
                        appModel.extractVocabulary(languageOption, projectName, revisionName, dirPath);
                        SecondaryWindowsInit.successWindow();
                    }
                }
            }
        }
    }
}
