package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Core.GUI;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;


public class LobbyController {
    private GUI gui;
    private ClientController clientController;
    private SceneManager sceneManager;

    public void setController(GUI gui,ClientController clientCtrl,SceneManager sceneManager) {
        this.gui = gui;
        this.clientController = clientCtrl;
        this.sceneManager = sceneManager;
    }

    @FXML AnchorPane rootPane;
    @FXML private ScrollPane gamesContainer;
    @FXML private VBox gamesVBox;
    @FXML private ComboBox<Integer> playersComboBox;
    @FXML private Label errorLabel;
    @FXML private Button createGameButton;


    @FXML public void initialize() {

        //riempo il comboBox con i possibili giocatori di una partita
        playersComboBox.getItems().addAll(2,3,4,5);

        //bottone di createNewGame inizialmente disabilitato di default che si abilità se viene scelto un numero di
        //giocatori
        playersComboBox.valueProperty().addListener((obs,oldVal,newVal)-> {
            createGameButton.setDisable(newVal==null);
        });

        // effetto click
            createGameButton.setOnMousePressed(e -> {
            createGameButton.setScaleX(0.95);
            createGameButton.setScaleY(0.95);
        });

        createGameButton.setOnMouseReleased(e -> {
            createGameButton.setScaleX(1.0);
            createGameButton.setScaleY(1.0);
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

    //TODO da modificare per rendere l'aggiornamento piu intelligente senza eliminare ogni volta le gameCard
    public void updateLobby(List<LobbyInfoDTO> lobby){
        //pulisco l'interfaccia vecchia
        gamesVBox.getChildren().clear();

        //rifaccio e visualizzo l'interfaccia nuova
        for (LobbyInfoDTO dto : lobby) {
            System.out.println("CREO CARD: " + dto);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameCard.fxml"));

                Parent card = loader.load();

                GameCardController controller = loader.getController();
                controller.setData(dto,gui,clientController,sceneManager);

                gamesVBox.getChildren().add(card);

            } catch (Exception e) {
                System.out.println("ERRORE NEL CREARE LE GAMECARD: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void showMessage(String message){
        errorLabel.setText(message);
        errorLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        errorLabel.setVisible(true);
    }


    @FXML
    public void handleCreateGame() {
        sceneManager.loadTotemScene(-1,playersComboBox.getValue(),null);
    }
}
