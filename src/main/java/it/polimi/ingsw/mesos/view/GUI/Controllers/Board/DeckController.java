package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * Controller for the deck slot on the game board.
 * Displays the back face of the current era deck based on the era string received from the server.
 * Updates the image only when the era changes.
 */
public class DeckController {

    @FXML StackPane deckSlot;
    @FXML ImageView deckImage;

    /**
     * Updates the deck image to match the given era.
     * Resolves the image path from the era string and loads it from resources.
     * If the era is not recognised, logs an error and returns without changing the image.
     *
     * @param era the current era string, expected values are "I", "II" or "III"
     */
    public void updateDeckView(String era) {
        String path = switch(era) {
            case "I"   -> "/images/era/CH_ERA_1.png";
            case "II"  -> "/images/era/CH_ERA_2.png";
            case "III" -> "/images/era/CH_ERA_3.png";
            default    -> null;
        };
        if (path == null) {
            System.out.println("ERA NON RICONOSCIUTA: " + era);
            return;
        }

        try{
            Image image = new Image(Objects.requireNonNull(DeckController.class.getResourceAsStream(path)));
            deckImage.setImage(image);
        }  catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELL' IMMAGINE DEL DECK " + e.getMessage());
            e.printStackTrace();
        }


    }
}
