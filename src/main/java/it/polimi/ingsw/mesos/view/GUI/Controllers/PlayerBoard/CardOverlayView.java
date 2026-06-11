package it.polimi.ingsw.mesos.view.GUI.Controllers.PlayerBoard;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.CardView;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that displays a overlay showing all cards of a given type.
 * The overlay can be dismissed by clicking outside the card panel.
 * Only one overlay can be shown at a time.
 */
public class CardOverlayView {

    /** Whether an overlay is currently being displayed. Prevents multiple overlays at once. */
    private static boolean isShowing = false;

    /**
     * Shows the card overlay with the given list of card ids.
     * Loads and displays up to 24 card images arranged in rows of 8.
     * Blurs the existing scene content and adds a transparent click layer to dismiss the overlay.
     * Does nothing if an overlay is already showing or if no scene is available.
     *
     * @param cardIds the list of card ids to display in the overlay
     */
    public static void show(List<String> cardIds) {
        Scene scene = getCurrentScene();
        if (scene == null) return;

        AnchorPane rootPane = (AnchorPane) scene.getRoot();

        if (isShowing) return;
        isShowing = true;

        // carica immagini (max 24)
        List<ImageView> imageViews = new ArrayList<>();
        List<String> limited = cardIds.subList(0, Math.min(cardIds.size(), 24));

        for (String id : limited) {
            ImageView iv = new ImageView();
            CardDTO dto = new CardDTO();
            dto.id=id;
            CardView.render(iv,dto);
            if (iv.getImage() == null) continue;
            iv.setFitWidth(85);
            iv.setFitHeight(145);
            iv.setPreserveRatio(true);
            iv.setOnMouseEntered(e -> UIEffects.applyOverlayCardHoverEffect(iv));
            iv.setOnMouseExited(e -> UIEffects.resetOverlayCardEffect(iv));

            imageViews.add(iv);
        }

        // righe da 8
        VBox rowsBox = new VBox(6);
        rowsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < imageViews.size(); i += 8) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);
            row.getChildren().addAll(imageViews.subList(i, Math.min(i + 8, imageViews.size())));
            rowsBox.getChildren().add(row);
        }

        // contenitore compatto con le carte
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setStyle(
                "-fx-background-color: rgba(10,10,30,0.72);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: rgba(255,215,0,0.55);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 14;"
        );
        content.getChildren().add(rowsBox);
        content.setPickOnBounds(true);
        content.setOnMouseClicked(javafx.event.Event::consume);

        // centra il content nel rootPane
        content.translateXProperty().bind(
                rootPane.widthProperty().subtract(content.widthProperty()).divide(2)
        );
        content.translateYProperty().bind(
                rootPane.heightProperty().subtract(content.heightProperty()).divide(2)
        );

        // salva figli esistenti e applica blur solo a loro
        List<Node> existingChildren = new ArrayList<>(rootPane.getChildren());
        Group blurGroup = new Group(existingChildren);
        blurGroup.setEffect(new GaussianBlur(5));
        rootPane.getChildren().clear();
        rootPane.getChildren().add(blurGroup);

        // layer trasparente per chiudere cliccando fuori
        Region clickLayer = new Region();
        clickLayer.setStyle("-fx-background-color: transparent;");
        AnchorPane.setTopAnchor(clickLayer, 0.0);
        AnchorPane.setBottomAnchor(clickLayer, 0.0);
        AnchorPane.setLeftAnchor(clickLayer, 0.0);
        AnchorPane.setRightAnchor(clickLayer, 0.0);

        clickLayer.setOnMouseClicked(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(120), content);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(ev -> {
                rootPane.getChildren().clear();
                rootPane.getChildren().addAll(existingChildren);
                isShowing = false;
            });
            ft.play();
        });

        // aggiungi clickLayer e content senza blur
        rootPane.getChildren().addAll(clickLayer, content);


        // fade-in
        content.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(150), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Retrieves the scene of the currently showing JavaFX stage.
     * Returns null if no stage is currently visible.
     *
     * @return the current Scene, or null if none is available
     */
    private static Scene getCurrentScene() {
        for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
            if (w instanceof javafx.stage.Stage stage && stage.isShowing()) {
                return stage.getScene();
            }
        }
        return null;
    }
}