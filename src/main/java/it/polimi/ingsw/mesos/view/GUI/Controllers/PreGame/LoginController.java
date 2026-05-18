package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressIndicator;
import it.polimi.ingsw.mesos.rete.ServerDiscoverer;


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
        portTextField.setText("1234");

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

        handleSearchLAN();
    }

    @FXML public void handleSearchLAN(){
        errorLabel.setText("⏳ Ricerca server in LAN (max 5 sec)...");
        errorLabel.setStyle("-fx-text-fill: #0078D7;");

        // Creo il Task asincrono per non far frizzare la GUI
        Task<String[]> discoveryTask = new Task<>() {
            @Override
            protected String[] call() {
                return ServerDiscoverer.discoverServerInfo();
            }
        };

        //quando il Task finisce ritorno al thread grafico
        discoveryTask.setOnSucceeded(event -> {
            String[] serverInfo = discoveryTask.getValue();

            // Riabilito il bottone di connessione SOLO se c'è un nickname inserito
            connectToLobbyButton.setDisable(nicknameTextField.getText() == null || nicknameTextField.getText().isBlank());

            if (serverInfo != null) {
                // SERVER TROVATO!
                String ip = serverInfo[0];
                String socketPort = serverInfo[1];
                String rmiPort = serverInfo[2];

                ipTextField.setText(ip);
                if ("SOCKET".equals(networkComboBox.getValue())) {
                    portTextField.setText(socketPort);
                } else {
                    portTextField.setText(rmiPort);
                }

                errorLabel.setText("✔ Server trovato automaticamente!");
                errorLabel.setStyle("-fx-text-fill: green;");
            } else {
                // NESSUN SERVER
                errorLabel.setText("❌ Nessun server trovato. Inserisci IP manualmente.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        });

        discoveryTask.setOnFailed(event -> {
            connectToLobbyButton.setDisable(nicknameTextField.getText() == null || nicknameTextField.getText().isBlank());
            errorLabel.setText("❌ Errore di rete durante la ricerca.");
            errorLabel.setStyle("-fx-text-fill: red;");
        });

        // Lancio il Thread
        Thread t = new Thread(discoveryTask);
        t.setDaemon(true);
        t.start();
    }

    //valutare le scelte di indirizzo ip e porta e attenzione a errori e usare label per segnalarli
    @FXML public void handleConnect() {
        String nickname = nicknameTextField.getText();
        String ip = ipTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        String networkChoice = networkComboBox.getValue();

        gui.handleLogin(nickname,ip,port,networkChoice);
        gui.setNickname(nickname);
    }
}
