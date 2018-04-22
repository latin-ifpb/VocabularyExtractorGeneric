package org.bluebird.UserInterface.Utils;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public interface WindowsInit {

    /**
     * Inicializa uma janela do javafx
     * @param path Caminho do fxml
     */
    void WindowsInitializer(String path);

    /**
     * Fecha janela atual
     * @param button Elemento da janela que vai ser fechada
     */
    static void closeCurrentWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
