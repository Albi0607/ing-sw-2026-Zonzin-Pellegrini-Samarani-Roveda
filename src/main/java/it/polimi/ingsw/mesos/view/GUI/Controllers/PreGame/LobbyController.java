package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;


import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for the lobby screen.
 * Displays the list of available games as interactive cards and allows
 * the player to create a new game by selecting the number of players.
 * Updates the game list incrementally without recreating existing cards.
 */
public class LobbyController {
    /** The scene manager used for navigation between screens. */
    private SceneManager sceneManager;
    /** Maps each game id to its corresponding GameCardController for updates. */
    private final Map<Integer, GameCardController> cardControllers = new HashMap<>();

    // FXML components
    @FXML AnchorPane rootPane;
    @FXML private ScrollPane gamesContainer;
    @FXML private VBox gamesVBox;
    @FXML private ComboBox<Integer> playersComboBox;
    @FXML private Label errorLabel;
    @FXML private Button createGameButton;

    /**
     * Initializes the lobby screen.
     * Populates the players combo box with the allowed player counts,
     * disables the create button until a player count is selected,
     * and applies the click effect and background image.
     */
    @FXML public void initialize() {

        //riempo il comboBox con i possibili giocatori di una partita
        playersComboBox.getItems().addAll(2,3,4,5);

        //bottone di createNewGame inizialmente disabilitato di default che si abilità se viene scelto un numero di
        //giocatori
        playersComboBox.valueProperty().addListener((obs,oldVal,newVal)-> {
            createGameButton.setDisable(newVal==null);
        });

        // effetto click
        UIEffects.applyClickEffect(createGameButton);

        //metto come sfondo l'immagine di background mesos
        UIEffects.applyBackground(rootPane);
    }

    /**
     * Injects the scene manager into this controller.
     *
     * @param sceneManager the scene manager used for navigation
     */
    public void setController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Updates the lobby game list incrementally.
     * Adds new game cards for newly appeared games, updates data on existing ones,
     * and removes cards for games that are no longer present in the list.
     *
     * @param lobby the current list of available games received from the server
     */
    public void updateLobby(List<LobbyInfoDTO> lobby) {

        // rimuovo le card per lobby che non esistono piu
        Set<Integer> currentIds = lobby.stream()
                .map(dto -> dto.id)
                .collect(Collectors.toSet());

        cardControllers.entrySet().removeIf(entry -> {
            if (!currentIds.contains(entry.getKey())) {
                gamesVBox.getChildren().removeIf(node ->
                        node.getUserData() != null && node.getUserData().equals(entry.getKey())
                );
                return true;
            }
            return false;
        });

        // aggiorno o creo le card
        for (LobbyInfoDTO dto : lobby) {
            if (cardControllers.containsKey(dto.id)) {
                // card esiste gia, aggiorno solo i dati
                cardControllers.get(dto.id).setData(dto,sceneManager);
            } else {
                // card nuova, la creo
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameCard.fxml"));
                    Parent card = loader.load();
                    card.setUserData(dto.id);

                    GameCardController controller = loader.getController();
                    controller.setData(dto, sceneManager);
                    cardControllers.put(dto.id, controller);
                    gamesVBox.getChildren().add(card);

                } catch (Exception e) {
                    System.out.println("ERRORE NEL CREARE LE GAMECARD: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Displays a message in the error label with orange text.
     *
     * @param message the message to display
     */
    public void showMessage(String message){
        errorLabel.setText(message);
        errorLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        errorLabel.setVisible(true);
    }

    /**
     * Handles the create game button action.
     * Navigates to the totem choice screen in create mode,
     * passing -1 as the game id to signal that a new game should be created.
     */
    @FXML
    public void handleCreateGame() {
        sceneManager.loadTotemScene(-1,playersComboBox.getValue(),null);
    }
}
