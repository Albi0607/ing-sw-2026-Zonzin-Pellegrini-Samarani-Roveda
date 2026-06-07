package it.polimi.ingsw.mesos.view.GUI.Controllers.PlayerBoard;

import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

public class PlayerCardView {

    public static void addNewCard(String id, StackPane stackPane, ImageView imageView, Label label, List<String> cardIds) {

        if (!stackPane.isVisible()) {
            stackPane.setVisible(true);
            stackPane.setManaged(true);
        }

        int count = 0;
        try {
            count = Integer.parseInt(label.getText());
            label.setText(String.valueOf(++count));
            label.setStyle(
                    "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-color: rgba(0,0,0,0.70);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 1 4 1 4;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0.5, 0, 0);"
            );
        } catch (Exception e){
            System.out.println("ERRORE NEL MOSTARE LA LABEL PER IL NUMERO DI CARTE: " + e.getMessage());
            e.printStackTrace();
        }

        Image img = loadCardImage(id);
        if(img!=null) {
            imageView.setImage(img);
        }

        // hover
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

        // click — apre overlay con tutte le carte della tipologia
        if (cardIds != null && !cardIds.isEmpty()) {
            stackPane.setOnMouseClicked(e -> CardOverlayView.show(cardIds));
        }
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

