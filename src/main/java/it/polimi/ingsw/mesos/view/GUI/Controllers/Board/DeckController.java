package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class DeckController {

    @FXML StackPane deckSlot;
    @FXML ImageView deckImage;
    String path;

    //aggiorna l'immagine del deck solo quando cambia era
    public void updateDeckView(String era) {
        switch(era) {
            case "I":
                path="/images/era/CH_ERA_1.png";
                break;
            case "II":
                path="/images/era/CH_ERA_2.png";
                break;
            case"III":
                path="/images/era/CH_ERA_3.png";
                break;
        }

        try{
            Image image = new Image(Objects.requireNonNull(DeckController.class.getResourceAsStream(path)));
            deckImage.setImage(image);
        }  catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELL' IMMAGINE DEL DECK " + e.getMessage());
            e.printStackTrace();
        }


    }
}
