package org.bluebird.UserInterface.App;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.bluebird.UserInterface.Utils.WindowsInit;

public class SuccessController {

    @FXML
    private Button confirmButton;

    /**
     * Espera o usuario confirmar que foi concluido a extracao
     */
    @FXML
    public void confirmExtraction() {
        WindowsInit.closeCurrentWindow(confirmButton);
    }
}
