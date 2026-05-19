package it.polimi.ingsw.mesos.view.GUI.Controllers;

import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Objects;

public class PlayerCardView {

    public static void addNewCard(String id, StackPane stackPane, ImageView imageView, Label label) {

        System.out.println("immagine stampata");
        //abilito lo slot se è la prima chiamata
        if (!stackPane.isVisible()) {
            stackPane.setVisible(true);
            stackPane.setManaged(true);
        }

        //aggiorno contatore
        int count = 0;
        try {
            count = Integer.parseInt(label.getText());
            label.setText(String.valueOf(++count));
        }catch (Exception e){
            System.out.println("ERRORE NEL MOSTARE LA LABEL PER IL NUMERO DI CARTE: " + e.getMessage());
            e.printStackTrace();
        }

        //stampo immagine
        Image img = loadCardImage(id);
        if(img!=null) {
            imageView.setImage(img);
        }

        //aggiungo effetto hover
        stackPane.setOnMouseEntered(e -> {
            stackPane.setEffect(new DropShadow(20, Color.BLACK));
            stackPane.setScaleX(1.05);
            stackPane.setScaleY(1.05);
        });

        stackPane.setOnMouseExited(e -> {
            stackPane.setEffect(null);
            stackPane.setScaleX(1.0);
            stackPane.setScaleY(1.0);
        });

        // aggiungo effetto click
        /*
        stackPane.setOnMouseClicked((MouseEvent e) -> {
            CardOverlayView.show(id, count, imageView.getImage());
        });
        */
    }

    private static Image loadCardImage(String id) {
        String path;
        if(id.startsWith("CH")){
            path = "/images/characters/" + id + ".png";
        } else if(id.startsWith("BD")){
            path = "/images/buildings/" + id + ".png";
        } else{
            return null;
        }
        try {
            return new Image(Objects.requireNonNull(PlayerCardView.class.getResourceAsStream(path)));
        } catch (Exception e) {
            System.out.println("ERRORE NEL CARICAMENTO DELLE CARTE IN PLAYERBOARD " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

