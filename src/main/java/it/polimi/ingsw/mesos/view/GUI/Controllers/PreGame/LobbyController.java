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
    }

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


    @FXML
    public void handleCreateGame() {
        sceneManager.loadTotemScene(-1,playersComboBox.getValue(),null);
    }
}
