package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;


public class LoginController {
    private GUI gui;

    public void setController(GUI gui) {
        this.gui = gui;
    }

    @FXML private AnchorPane rootPane;

    @FXML private TextField nicknameTextField;

    @FXML private TextField ipTextField;

    @FXML private TextField portTextField;

    @FXML private ComboBox<String> networkComboBox;

    @FXML private Label errorLabel;

    @FXML private Button connectToLobbyButton;

    @FXML private TextField clientIpTextField;

    @FXML public void initialize() {

        //Riempio il ComboBox con le opzioni di rete e metto di default SOCKET
        networkComboBox.getItems().addAll("SOCKET", "RMI");
        networkComboBox.setValue("SOCKET");

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

        //metto come sfondo l'immagine di background mesos
        try {
            Image image = new Image(getClass().getResource("/images/tool/backgroundMesos.png").toExternalForm());

            ImageView background = new ImageView(image);

            // dimensione base (quella che vuoi tu)
            background.setFitWidth(1450);
            background.setFitHeight(750);

            // scala proporzionalmente
            background.setPreserveRatio(false); // oppure true se vuoi mantenere proporzioni

            // IMPORTANTISSIMO: si adatta al resize del pane
            background.fitWidthProperty().bind(rootPane.widthProperty());
            background.fitHeightProperty().bind(rootPane.heightProperty());

            // manda sul fondo
            rootPane.getChildren().add(0, background);
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELL'IMMAGINE DI BACKGROUND: " + e.getMessage());
            e.printStackTrace();
        }

    }

    //valutare le scelte di indirizzo ip e porta e attenzione a errori e usare label per segnalarli
    @FXML public void handleConnect() {
        String nickname = nicknameTextField.getText();
        String ip = ipTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        String clientIp = clientIpTextField.getText();
        String networkChoice = networkComboBox.getValue();

        gui.handleLogin(nickname,ip,port,networkChoice,clientIp);
        gui.setNickname(nickname);
    }

    public void showLoginError(String errorMessage) {
        if (errorLabel != null) {
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            errorLabel.setText("❌ Nickname già in uso. Scegline un altro!");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        } else {
            System.err.println("[ERRORE CRITICO] errorLabel è NULL! Controlla se nel file FXML c'è fx:id=\"errorLabel\"");
        }
        nicknameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px;");
        nicknameTextField.clear();
        nicknameTextField.requestFocus();

    }
}
