package edu.uoc.uocycle.view;

import edu.uoc.uocycle.UOCycle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class CreditsViewController {

    /**
     * This button is used to go back to the "main" scene.
     */
    @FXML private Button backButton;

    /**
     * This container is used to apply hover effects on the back button.
     */
    @FXML private StackPane backButtonContainer;

    /**
     * This method is called by the JavaFX framework when the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        UiEffects.applyHoverTranslate(backButton, backButtonContainer, 3, 3, Duration.millis(100));
    }

    /**
     * It goes to the "main" scene.
     */
    @FXML
    public void backMain() {
        try {
            UOCycle.main.goScene("main");
        } catch (IOException e) {
            System.exit(1);
        }
    }
}