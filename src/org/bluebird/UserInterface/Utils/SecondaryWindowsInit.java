package org.bluebird.UserInterface.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SecondaryWindowsInit implements WindowsInit {

    private SecondaryWindowsInit(String path) {
        WindowsInitializer(path);
    }

    @Override
    public void WindowsInitializer(String path) {
        try {
            Stage appWindow = new Stage();
            Pane appWindowRoot = FXMLLoader.load(getClass().getResource(path));

            Scene scene = new Scene(appWindowRoot);
            appWindow.setScene(scene);
            appWindow.setResizable(false);
            appWindow.setTitle("VocabularyExtractor");
            appWindow.show();

        } catch (IOException error) {
            error.printStackTrace();
            System.exit(1);
        }
    }

    public static void successWindow() {
        new SecondaryWindowsInit("/resources/fxml/success.fxml");
    }
}
