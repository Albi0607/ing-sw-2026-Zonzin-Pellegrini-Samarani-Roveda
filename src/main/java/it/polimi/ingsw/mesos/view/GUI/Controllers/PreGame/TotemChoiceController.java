package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller for the totem choice screen.
 * Allows the player to select a color totem before creating or joining a game.
 * Displays which totems are already taken by other players and updates in real time
 * when the lobby data changes.
 */
public class TotemChoiceController {
    /** The client controller used to send the create or join game request to the server. */
    private ClientController clientController;
    /** The scene manager used to navigate to the waiting room after confirming the choice. */
    private SceneManager sceneManager;

    /** The color currently selected by the player, or null if no color has been chosen yet. */
    private Color colorChoice;
    /** The lobby data for the game being joined, used to check which colors are already taken. */
    private LobbyInfoDTO dto;
    /** The game id: -1 when creating a new game, or the actual game id when joining. */
    private int id;
    /** The number of players chosen for the new game, used only in create mode. */
    private int numPlayers;

    // FXML components

    /** Ordered list of all totem StackPane slots, parallel to totemColors. */
    private List<StackPane> totemSlots;
    /** Ordered list of colors corresponding to each totem slot, parallel to totemSlots. */
    private List<Color> totemColors;

    @FXML AnchorPane rootPane;
    @FXML StackPane blueTotemSlot;
    @FXML ImageView blueTotemImage;
    @FXML StackPane yellowTotemSlot;
    @FXML ImageView yellowTotemImage;
    @FXML StackPane purpleTotemSlot;
    @FXML ImageView purpleTotemImage;
    @FXML StackPane whiteTotemSlot;
    @FXML ImageView whiteTotemImage;
    @FXML StackPane redTotemSlot;
    @FXML ImageView redTotemImage;
    @FXML Label errorLabel;
    @FXML Button create_joinGameButton;


    /**
     * Loads all totem images, builds the parallel slot and color lists,
     * disables the confirm button and registers mouse event handlers on all slots.
     */
    @FXML void initialize() {

        UIEffects.applyClickEffect(create_joinGameButton);
        UIEffects.applyBackground(rootPane);

        //carico tutte le immagini dei totem
        try {
            blueTotemImage.setImage(new Image(getClass().getResourceAsStream("/images/totem/BLUE.png")));
            yellowTotemImage.setImage(new Image(getClass().getResourceAsStream("/images/totem/YELLOW.png")));
            purpleTotemImage.setImage(new Image(getClass().getResourceAsStream("/images/totem/PURPLE.png")));
            whiteTotemImage.setImage(new Image(getClass().getResourceAsStream("/images/totem/WHITE.png")));
            redTotemImage.setImage(new Image(getClass().getResourceAsStream("/images/totem/RED.png")));


            //creo liste per poter scorrere tutti i totem per attivare effetti
            totemSlots = List.of(blueTotemSlot, yellowTotemSlot, purpleTotemSlot, whiteTotemSlot, redTotemSlot);
            totemColors = List.of(Color.BLUE, Color.YELLOW, Color.PURPLE, Color.WHITE, Color.RED);

            //disabilito il bottone finché non viene scelto un totem
            create_joinGameButton.setDisable(true);

            // setup di tutti i totem
            setupEvents();
            refresh();

        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLE IMMAGINI DEI TOTEM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Injects dependencies into this controller.
     *
     * @param clientController the client controller used to create or join a game
     * @param sceneManager the scene manager used for change the scene when create/join game
     */
    public void setController(ClientController clientController, SceneManager sceneManager){
        this.clientController = clientController;
        this.sceneManager = sceneManager;
    }

    /**
     * Sets the parameters for this screen depending on whether the player
     * is creating a new game or joining an existing one.
     * Updates the confirm button text accordingly.
     *
     * @param id -1 if creating a new game, the actual game id if joining
     * @param numPlayers the number of players for the new game, used only in create mode
     * @param dto the lobby data for the game being joined, or null in create mode
     */
    public void setParameter(int id, int numPlayers,LobbyInfoDTO dto){
        this.id = id;
        this.numPlayers = numPlayers;
        if(dto!=null) {
            this.dto = dto;
        }

        //scrivo nel bottone il testo adatto alla scelta fatta dall'utente
        if(id == -1) {
            create_joinGameButton.setText("CREATE GAME");
        }
        else {
            create_joinGameButton.setText("JOIN GAME");
        }
    }

    /**
     * Updates the lobby data for this screen when another player picks a color.
     * Searches the updated lobby list for the game matching the current dto id.
     * If the data has not changed, no refresh is performed.
     * If the game is now full, shows an error message and redirects to the lobby after 2 seconds.
     * Otherwise, refreshes the totem slots to reflect the updated color availability.
     *
     * @param lobby the updated list of all lobbies received from the server
     */
    public void updateDTO(List<LobbyInfoDTO> lobby) {
        if(this.dto==null) {
            return;
        }

        for (LobbyInfoDTO dto : lobby) {
            if (this.dto.id == dto.id) {
                if(this.dto.equals(dto)){
                    return;
                }
                this.dto = dto;
                break;
            }
        }

        if (this.dto.numPlayers >= this.dto.maxNumPlayers) {
            errorLabel.setText("Partita piena, ritorno alla lobby...");
            errorLabel.setTextFill(javafx.scene.paint.Color.RED);
            errorLabel.setVisible(true);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> sceneManager.loadLobbyScene(clientController));
            delay.play();
        } else {
            refresh();
        }
    }

    /**
     * Registers hover and click mouse event handlers on all totem slots.
     * Hovering over a free unselected slot highlights it in green.
     * Clicking a taken slot shows a red flash via showTaken.
     * Clicking a free slot selects its color and triggers a full refresh.
     */
    private void setupEvents() {

        for (int i = 0; i < totemSlots.size(); i++) {

            StackPane slot = totemSlots.get(i);
            Color color = totemColors.get(i);

            slot.setOnMouseEntered(e -> {
                if (isTaken(color) || color.equals(colorChoice)) return;
                slot.setStyle("""
                        -fx-background-color: #66ff66;
                        -fx-background-radius: 15;
                        -fx-border-color: white;
                        -fx-border-width: 2;
                        -fx-border-radius: 15;
                        """);
            });

            slot.setOnMouseExited(e -> {
                refresh();
            });

            slot.setOnMouseClicked(e -> {

                if (isTaken(color)) {
                    showTaken(slot);
                    return;
                }

                colorChoice = color;
                refresh();
            });
        }
    }

    /**
     * Refreshes the visual state of all totem slots.
     * Taken slots are shown as dark.
     * The selected slot is highlighted in bright green with a white border.
     * Free unselected slots are shown in neutral dark gray.
     * Also updates the confirm button enabled state.
     */
    private void refresh() {

        for (int i = 0; i < totemSlots.size(); i++) {

            StackPane slot = totemSlots.get(i);
            Color color = totemColors.get(i);

            if (isTaken(color)) {
                slot.setStyle("""
                        -fx-background-color: #333333;
                        -fx-opacity: 0.5;
                        -fx-background-radius: 15;
                        """);
            }
            else if (color.equals(colorChoice)) {
                slot.setStyle("""
                        -fx-background-color: #00cc00;
                        -fx-background-radius: 15;
                        -fx-border-color: white;
                        -fx-border-width: 3;
                        -fx-border-radius: 15;
                        """);
            }
            else {
                slot.setStyle("""
                        -fx-background-color: #4a4a4a;
                        -fx-background-radius: 15;
                        -fx-border-color: gray;
                        -fx-border-width: 2;
                        -fx-border-radius: 15;
                        """);
            }
        }
        //rende il bottone attivo se viene scelto un colore
        updateButtonState();
    }

    /**
     * Returns whether the given color has already been taken by another player.
     *
     * @param color the color to check
     * @return true if the color is present in the dto's taken colors list, false otherwise
     */
    private boolean isTaken(Color color) {
        return dto!=null && dto.takenColors!=null && dto.takenColors.contains(color);
    }

    /**
     * Briefly highlights a totem slot in red to indicate it is already taken.
     * The slot will return to its normal state on the next refresh call.
     *
     * @param slot the slot to highlight in red
     */
    private void showTaken(StackPane slot) {
        slot.setStyle("""
                -fx-background-color: #ff0000;
                -fx-background-radius: 15;
                -fx-border-color: darkred;
                -fx-border-width: 3;
                -fx-border-radius: 15;
                """);
    }

    /**
     * Enables the confirm button only if a color has been selected.
     * Disables it if colorChoice is null.
     */
    private void updateButtonState() {
        create_joinGameButton.setDisable(colorChoice == null);
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
        create_joinGameButton.setDisable(colorChoice == null);
    }

    /**
     * Handles the create or join game button action.
     * If no color has been selected, shows an error message and returns.
     * Disables the button immediately to prevent double clicks while waiting
     * for the server response. The scene transition to the waiting room is
     * triggered automatically by updateClientState when the server confirms
     * the join. If the server rejects the request (e.g. game full or color taken),
     * the button is re-enabled and an error message is shown.
     *
     */
    @FXML public void handleCreate_JoinGame() {

        if(colorChoice == null) {
            errorLabel.setText("SELECT A TOTEM");
            errorLabel.setDisable(false);
            return;
        }
        create_joinGameButton.setDisable(true);

        try {
            if(id == -1) {
                clientController.createNewGame(numPlayers, colorChoice);
            }
            else {
                clientController.joinGame(id, colorChoice);
            }

        } catch (Exception e) {
            create_joinGameButton.setDisable(false);
            errorLabel.setText("ERRORE NEL CREARE/ACCEDERE ALLA PARTITA");
            System.out.println("ERRORE ALL'ATTIVAZIONE DEI METODI PER ACCEDERE/CREARE LA PARTITA IN TOTEMCHOICE :" + e.getMessage());
            e.printStackTrace();
        }
    }
}
