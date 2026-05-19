package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;


public class PlayerBoardController {
    private ObservablePlayerModel playerModel;
    private String colorPaint;

    //tengo riferimento alle carte per tipologia
    private final List<String> artists = new ArrayList<>();
    private final List<String> builders = new ArrayList<>();
    private final List<String> gatherers = new ArrayList<>();
    private final List<String> hunters = new ArrayList<>();
    private final List<String> inventors = new ArrayList<>();
    private final List<String> shamans = new ArrayList<>();


    List<List<String>> characters;
    List<StackPane> stackPanes;
    List<ImageView> imagesView;
    List<Label> cardsCount;


    @FXML Label playerNameLabel;
    @FXML Label foodPointsLabel;
    @FXML Label prestigePointsLabel;

    @FXML StackPane cardSlot1;
    @FXML ImageView cardImage1;
    @FXML Label cardCount1;
    @FXML StackPane cardSlot2;
    @FXML ImageView cardImage2;
    @FXML Label cardCount2;
    @FXML StackPane cardSlot3;
    @FXML ImageView cardImage3;
    @FXML Label cardCount3;
    @FXML StackPane cardSlot4;
    @FXML ImageView cardImage4;
    @FXML Label cardCount4;
    @FXML StackPane cardSlot5;
    @FXML ImageView cardImage5;
    @FXML Label cardCount5;
    @FXML StackPane cardSlot6;
    @FXML ImageView cardImage6;
    @FXML Label cardCount6;
    @FXML StackPane cardSlot7;
    @FXML ImageView cardImage7;
    @FXML Label cardCount7;


    @FXML
    public void initialize() {

        //in questo modo se non ci sono carte di un tipo non occupo spazio
        disableSlot(cardSlot1);
        disableSlot(cardSlot2);
        disableSlot(cardSlot3);
        disableSlot(cardSlot4);
        disableSlot(cardSlot5);
        disableSlot(cardSlot6);
        disableSlot(cardSlot7);

        characters = List.of(artists, builders, gatherers, hunters, inventors, shamans);

        stackPanes = List.of(cardSlot1, cardSlot2, cardSlot3, cardSlot4, cardSlot5, cardSlot6);

        imagesView = List.of(cardImage1, cardImage2, cardImage3, cardImage4, cardImage5, cardImage6);

        cardsCount = List.of(cardCount1, cardCount2, cardCount3, cardCount4, cardCount5, cardCount6);
    }

    private void disableSlot(StackPane slot) {
        slot.setVisible(false);
        slot.setManaged(false);
    }


    public void setPlayer(ObservablePlayerModel playerModel) {
        this.playerModel = playerModel;

        bind();
    }

    private void bind() {
        //trova il colore del player e scrive le label con quel colore
        colorPaint = switch (playerModel.getColor()) {
            case BLUE -> "blue";
            case YELLOW -> "yellow";
            case PURPLE -> "purple";
            case WHITE -> "white";
            case RED -> "red";
            default -> "black";
        };

        //aggiorna automaticamente il nome (non serve)
        playerNameLabel.textProperty().bind(playerModel.nicknameProperty());
        playerNameLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        //aggiorna automaticamente il cibo
        foodPointsLabel.textProperty().bind(playerModel.foodProperty().asString());
        foodPointsLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        //aggiorna automaticamente i punti prestigio
        prestigePointsLabel.textProperty().bind(playerModel.prestigePointsProperty().asString());
        prestigePointsLabel.setStyle("-fx-text-fill: " + colorPaint + ";" + "-fx-font-size: 22px;" + "-fx-font-weight: bold;");

        playerModel.getTribe().getCharacters().addListener((ListChangeListener<? super CardDTO>) obs -> {
            List<CardDTO> list = playerModel.getTribe().getCharacters();
            CardDTO newCard = list.get(list.size() - 1);
            addCharacterCard(newCard);
        });

        playerModel.getTribe().getBuildings().addListener((ListChangeListener<? super CardDTO>) obs -> {
            List<CardDTO> list = playerModel.getTribe().getBuildings();
            CardDTO newCard = list.get(list.size() - 1);
            addBuildingCard(newCard);
        });

    }

    private void addBuildingCard(CardDTO card) {

        Platform.runLater(() -> PlayerCardView.addNewCard(card.id, cardSlot7, cardImage7, cardCount7));
    }

    private void addCharacterCard(CardDTO card) {
        if(card.characterType==null){
            return;
        }

        int selector = switch (card.characterType) {
            case ARTIST -> 0;
            case BUILDER -> 1;
            case GATHERER -> 2;
            case HUNTER -> 3;
            case INVENTOR -> 4;
            case SHAMAN -> 5;
        };

        characters.get(selector).add(card.id);

        Platform.runLater(()->
                PlayerCardView.addNewCard(card.id, stackPanes.get(selector), imagesView.get(selector), cardsCount.get(selector)));

    }

}

