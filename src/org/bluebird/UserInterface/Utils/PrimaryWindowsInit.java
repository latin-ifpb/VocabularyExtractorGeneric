package org.bluebird.UserInterface.Utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class PrimaryWindowsInit implements WindowsInit {

    /**
     * Cria uma janela primaria do javafx
     * @param path Caminho do fxml
     */
    private PrimaryWindowsInit(String path) {
        WindowsInitializer(path);
    }

    /**
     * Inicializa uma janela do javafx
     * @param path Caminho do fxml
     */
    @Override
    public void WindowsInitializer(String path) {
        try {
            Stage appWindow = new Stage();
            Pane appWindowRoot = FXMLLoader.load(getClass().getResource(path));

            Scene scene = new Scene(appWindowRoot);
            appWindow.setScene(scene);
            appWindow.setResizable(false);
            appWindow.setTitle("VocabularyExtractor");
            appWindow.setOnCloseRequest(e -> Platform.exit());
            appWindow.show();

        } catch (IOException error) {
            System.exit(1);
        }
    }

    /**
     * Cria a janela do app
     */
    public static void appWindow() {
        new PrimaryWindowsInit("/resources/fxml/app.fxml");
    }

}
