package edu.uoc.uocycle.view;

import edu.uoc.uocycle.UOCycle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class MainViewController {


    /**
     * This button is used to go to the "play" scene.
     */
    @FXML private Button loginButton;

    /**
     * This button is used to go to the "credits" scene.
     */
    @FXML private Button creditsButton;

    /**
     * This container is used to apply hover effects on the login button.
     */
    @FXML private StackPane loginButtonContainer;

    /**
     * This container is used to apply hover effects on the credits button.
     */
    @FXML private StackPane creditsButtonContainer;

    /**
     * This method is called by the JavaFX framework when the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        UiEffects.applyHoverTranslate(loginButton, loginButtonContainer, 3, 3, Duration.millis(100));
        UiEffects.applyHoverTranslate(creditsButton, creditsButtonContainer, 3, 3, Duration.millis(100));
    }

    /**
     * It goes to the "play" scene.
     */
    @FXML
    public void newLogin(){
        try{
            UOCycle.main.goScene("play");
        }catch(IOException e){
            System.exit(1);
        }
    }

    /**
     * It goes to the "credits" scene.
     */
    @FXML
    public void readCredits(){
        try{
            UOCycle.main.goScene("credits");
        }catch(IOException e){
            System.exit(1);
        }
    }

}
