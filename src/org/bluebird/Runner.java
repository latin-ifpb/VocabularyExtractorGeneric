package org.bluebird;

import javafx.application.Application;
import javafx.stage.Stage;
import org.bluebird.UserInterface.Utils.PrimaryWindowsInit;

public class Runner extends Application {

    public void start(Stage primaryStage) {

        PrimaryWindowsInit.appWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
