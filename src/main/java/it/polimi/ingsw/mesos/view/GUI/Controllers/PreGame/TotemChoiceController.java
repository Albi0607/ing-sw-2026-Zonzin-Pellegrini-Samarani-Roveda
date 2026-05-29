package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class TotemChoiceController {
    private ClientController clientController;
    private SceneManager sceneManager;
    private Color colorChoice;
    private LobbyInfoDTO dto;

    private int id;
    private int numPlayers;

    private List<StackPane> totemSlots;
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


    public void setController(ClientController clientController, SceneManager sceneManager){
        this.clientController = clientController;
        this.sceneManager = sceneManager;

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

    public void setParameter(int id, int numPlayers,LobbyInfoDTO dto){
        this.id = id;
        this.numPlayers = numPlayers;
        if(dto!=null) {
            this.dto = dto;
        }

        //scrivo nel bottone il testo adatto alla scelta fatta dall'utente
        if(id == 0) {
            create_joinGameButton.setText("CREATE GAME");
        }
        else {
            create_joinGameButton.setText("JOIN GAME");
        }
    }

    //fa update del dto nel caso qualcuno abbia fatto l'accesso dalla lobby e abbia scelto un colore
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
        refresh();
    }


    @FXML void initialize() {

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

    private void setupEvents() {

        for (int i = 0; i < totemSlots.size(); i++) {

            StackPane slot = totemSlots.get(i);
            Color color = totemColors.get(i);

            slot.setOnMouseEntered(e -> {
                if (isTaken(color) || colorChoice == color) return;
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

    //metodo refresh per aggiornare eventuali totem presi o totem scelto
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
            else if (colorChoice == color) {
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

    //metodo per capire se uno slot è gia occupato
    private boolean isTaken(Color color) {
        return dto!=null && dto.takenColors!=null && dto.takenColors.contains(color);
    }

    //metodo che mostra che il totem è gia stato preso
    private void showTaken(StackPane slot) {
        slot.setStyle("""
                -fx-background-color: #ff0000;
                -fx-background-radius: 15;
                -fx-border-color: darkred;
                -fx-border-width: 3;
                -fx-border-radius: 15;
                """);
    }

    //metodo che rende abilitato il bottone se è stato scelto un colore di totem
    private void updateButtonState() {
        create_joinGameButton.setDisable(colorChoice == null);
    }

    public void showMessage(String message){
        errorLabel.setText(message);
        errorLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        errorLabel.setVisible(true);
    }

    //gestione del bottone che crea o accede al gioco
    @FXML public void handleCreate_JoinGame() {

        if(colorChoice == null) {
            errorLabel.setText("SELECT A TOTEM");
            errorLabel.setDisable(false);
            return;
        }
        try {
            if(id == -1) {
                clientController.createNewGame(numPlayers, colorChoice);
            }
            else {
                clientController.joinGame(id, colorChoice);
            }
            sceneManager.loadWaitingRoom();

        } catch (Exception e) {
            errorLabel.setText("ERRORE NEL CREARE/ACCEDERE ALLA PARTITA");
            System.out.println("ERRORE ALL'ATTIVAZIONE DEI METODI PER ACCEDERE/CREARE LA PARTITA IN TOTEMCHOICE :" + e.getMessage());
            e.printStackTrace();
        }
    }
}
