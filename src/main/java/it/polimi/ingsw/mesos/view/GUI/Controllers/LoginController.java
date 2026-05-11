package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class LoginController {
    private GUI gui;

    public void setController(GUI gui) {
        this.gui = gui;
    }
    @FXML private TextField nicknameTextField;

    @FXML private TextField ipTextField;

    @FXML private TextField portTextField;

    @FXML private ComboBox<String> networkComboBox;

    @FXML private Label errorLabel;

    @FXML private Button connectToLobbyButton;

    @FXML public void initialize() {

        //Riempio il ComboBox con le opzioni di rete e metto di default SOCKET
        networkComboBox.getItems().addAll("SOCKET", "RMI");
        networkComboBox.setValue("SOCKET");

        //default ip e port
        ipTextField.setText("127.0.0.1");
        portTextField.setText("12345");

        //rendo il bottone disabilitato finché il campo nickname è vuoto
        connectToLobbyButton.setDisable(true);
        nicknameTextField.textProperty().addListener((obs,oldVal,newVal)->{
            connectToLobbyButton.setDisable(newVal==null|| newVal.isEmpty());
        });


        // effetto click
        connectToLobbyButton.setOnMousePressed(e -> {
            connectToLobbyButton.setScaleX(0.95);
            connectToLobbyButton.setScaleY(0.95);
        });

        connectToLobbyButton.setOnMouseReleased(e -> {
            connectToLobbyButton.setScaleX(1.0);
            connectToLobbyButton.setScaleY(1.0);
        });

    }

    //valutare le scelte di indirizzo ip e porta
    @FXML public void handleConnect() {
        String nickname = nicknameTextField.getText();
        String ip = ipTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        String networkChoice = networkComboBox.getValue();

        gui.handleLogin(nickname,ip,port,networkChoice);
    }
}
