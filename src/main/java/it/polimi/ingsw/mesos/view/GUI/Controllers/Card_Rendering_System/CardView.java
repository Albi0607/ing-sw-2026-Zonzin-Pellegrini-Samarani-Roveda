package it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;


public class CardView {

    //capire come cambiare questa classe per non dovere metter if
    public static void render(ImageView imageView, CardDTO card) {

        if (card == null) {
            imageView.setImage(null);
            return;
        }

        String id = card.id;

        String path = getCardPath(id);

        try {

            Image image = new Image(Objects.requireNonNull(CardView.class.getResourceAsStream(path)));

            imageView.setImage(image);

        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELLA SINGOLA IMMAGINE: " + id + e.getMessage());
            e.printStackTrace();
        }
    }

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
