package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.ClientModel.CardDTO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Utility class responsible for rendering a card image into an ImageView.
 * Resolves the correct image path based on the card id prefix
 * and loads the image from the application resources.
 */
public class CardView {

    /**
     * Renders the image of the given card into the provided ImageView.
     * If the card is null, clears the ImageView.
     * The image path is resolved from the card id prefix via getCardPath.
     *
     * @param imageView the ImageView to render the card image into
     * @param card the card DTO whose image should be rendered, or null to clear
     */
    public static void render(ImageView imageView, CardDTO card) {

        if (card == null) {
            imageView.setImage(null);
            return;
        }

        String id = card.id;
        String path = getCardPath(id);
        if(path.isEmpty()){
            return;
        }

        try {
            Image image = new Image(Objects.requireNonNull(CardView.class.getResourceAsStream(path)));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELLA SINGOLA IMMAGINE: " + id + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Resolves the resource path of a card image based on its id prefix.
     * EV prefix maps to events, CH to characters, BD to buildings.
     * Returns an empty string if the prefix is not recognised.
     *
     * @param id the card id to resolve the path for
     * @return the resource path string for the card image
     */
    private static String getCardPath(String id) {

        if (id.startsWith("EV")) {
            return "/images/events/" + id + ".png";
        }

        if (id.startsWith("CH")) {
            return "/images/characters/" + id + ".png";
        }

        if (id.startsWith("BD")) {
            return "/images/buildings/" + id + ".png";
        }

        return "";
    }

}
