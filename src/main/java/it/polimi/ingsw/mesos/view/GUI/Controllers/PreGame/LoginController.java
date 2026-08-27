package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Controller for the login screen.
 * Handles user input for nickname, server IP, port, client IP and network type,
 * validates the input and delegates the connection to the GUI layer.
 */
public class LoginController {

    /** The main GUI instance used to trigger the login and set the nickname. */
    private GUI gui;

    // FXML components
    @FXML private AnchorPane rootPane;
    @FXML private TextField nicknameTextField;
    @FXML private TextField ipTextField;
    @FXML private TextField portTextField;
    @FXML private ComboBox<String> networkComboBox;
    @FXML private Label errorLabel;
    @FXML private Button connectToLobbyButton;
    @FXML private TextField clientIpTextField;

    /**
     * Initializes the login screen.
     * Sets default values for all input fields, configures the network combo box
     * to auto-update the port when the protocol changes, disables the connect
     * button until a nickname is entered, and applies the click effect and background.
     */
    @FXML public void initialize() {

        //Riempio il ComboBox con le opzioni di rete e metto di default SOCKET
        networkComboBox.getItems().addAll("SOCKET", "RMI");
        networkComboBox.setValue("SOCKET");
        UIEffects.applyParchmentCombo(networkComboBox);

        //default ip e port e clientIp
        ipTextField.setText("127.0.0.1");
        portTextField.setText("1234");
        clientIpTextField.setText("127.0.0.1");

        networkComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("SOCKET".equals(newVal)) {
                portTextField.setText("1234");
            } else if ("RMI".equals(newVal)) {
                portTextField.setText("1099");
            }
        });

        //rendo il bottone disabilitato finché il campo nickname è vuoto
        connectToLobbyButton.setDisable(true);
        nicknameTextField.textProperty().addListener((obs,oldVal,newVal)->{
            connectToLobbyButton.setDisable(newVal==null|| newVal.isEmpty());
            nicknameTextField.getStyleClass().remove("mesos-field-error");
        });

        // effetto click
        UIEffects.applyClickEffect(connectToLobbyButton);

        //immagine di sfondo
        UIEffects.applyLoginBackground(rootPane);
    }

    /**
     * Injects the main GUI instance into this controller.
     *
     * @param gui the GUI instance
     */
    public void setController(GUI gui) {
        this.gui = gui;
    }

    /**
     * Handles the connect button action.
     * Reads all input fields and delegates the login to the GUI layer.
     * Shows an error message if the port field does not contain a valid number.
     */
    @FXML public void handleConnect() {
        String nickname = nicknameTextField.getText();
        String ip = ipTextField.getText();
        String clientIp = clientIpTextField.getText();
        String networkChoice = networkComboBox.getValue();
        int port;
        try {
            port = Integer.parseInt(portTextField.getText());
        } catch (NumberFormatException e) {
            showLoginError("❌ Invalid port. Please enter a number.");
            return;
        }

        gui.handleLogin(nickname,ip,port,networkChoice,clientIp);
        gui.setNickname(nickname);
    }

    /**
     * Displays an error message on the login screen.
     * Highlights the nickname field in red, clears its content and requests focus
     * so the user can immediately type a new nickname.
     *
     * @param errorMessage the error message to display in the error label
     */
    public void showLoginError(String errorMessage) {
        if (errorLabel != null) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText(errorMessage);
            errorLabel.getStyleClass().setAll("mesos-error-label");
        } else {
            System.out.println("[ERRORE CRITICO] errorLabel è NULL! Controlla se nel file FXML c'è fx:id=\"errorLabel\"");
        }
        if (!nicknameTextField.getStyleClass().contains("mesos-field-error")) {
            nicknameTextField.getStyleClass().add("mesos-field-error");
        }
        nicknameTextField.clear();
        nicknameTextField.requestFocus();
    }
}
