package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.view.GUI.Controllers.Board.CardController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Utility class providing reusable JavaFX visual effects for UI components.
 * All methods are static since this class holds no state.
 */
public class UIEffects {

    // Background setup

    /**
     * Loads and applies the standard Mesos background image to the given AnchorPane.
     * The image is bound to the pane's width and height so it scales with the window.
     *
     * @param rootPane the AnchorPane to apply the background to
     */
    public static void applyBackground(AnchorPane rootPane) {
        try {
            Image image = new Image(UIEffects.class.getResource("/images/tool/backgroundMesos.png").toExternalForm());
            ImageView background = new ImageView(image);
            background.setPreserveRatio(false);
            background.fitWidthProperty().bind(rootPane.widthProperty());
            background.fitHeightProperty().bind(rootPane.heightProperty());
            rootPane.getChildren().add(0, background);
        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELL'IMMAGINE DI BACKGROUND: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Visual effects for buttons

    /**
     * Applies a subtle scale-down click effect to a button.
     * The button shrinks slightly on press and returns to normal on release,
     * giving tactile feedback to the user.
     *
     * @param button the button to apply the effect to
     */
    public static void applyClickEffect(Button button) {
        button.setOnMousePressed(e -> {
            button.setScaleX(0.95);
            button.setScaleY(0.95);
        });
        button.setOnMouseReleased(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    // Card effects

    /**
     * Applies a click visual effect to a card slot based on the card type.
     * Event cards show a red glow and reduced opacity to signal they are not playable.
     * Character cards show a lime glow with a slight scale down.
     * Building cards show an orange glow with a slight scale down.
     *
     * @param card the CardController to apply the effect to
     * @param cardId the id of the card, used to determine its type from the prefix
     */
    public static void applyCardClickEffect(CardController card, String cardId) {
        if (cardId.startsWith("EV")) {
            card.setEffect("-fx-effect: dropshadow(gaussian, red, 15, 0.9, 0, 0); -fx-opacity: 0.8;");
        } else if (cardId.startsWith("CH")) {
            card.setEffect("-fx-effect: dropshadow(gaussian, lime, 18, 0.8, 0, 0); -fx-scale-x: 0.95; -fx-scale-y: 0.95;");
        } else if (cardId.startsWith("BD")) {
            card.setEffect("-fx-effect: dropshadow(gaussian, orange, 18, 0.8, 0, 0); -fx-scale-x: 0.95; -fx-scale-y: 0.95;");
        }
    }

    /**
     * Applies a hover visual effect to a card slot based on the card type.
     * Event cards show a dark red glow with a not-allowed cursor to signal they are not playable.
     * All other card types show a yellow glow with a hand cursor.
     *
     * @param card the CardController to apply the effect to
     * @param cardId the id of the card, used to determine its type from the prefix
     */
    public static void applyCardHoverEffect(CardController card, String cardId) {
        if (cardId.startsWith("EV")) {
            card.setEffect("-fx-effect: dropshadow(gaussian, darkred, 12, 0.6, 0, 0); -fx-cursor: not-allowed;");
        } else {
            card.setEffect("-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0); -fx-cursor: hand;");
        }
    }

    /**
     * Removes any active visual effect from a card slot, restoring its default appearance.
     *
     * @param card the CardController to reset the effect on
     */
    public static void applyCardExitEffect(CardController card) {
        card.resetEffect();
    }

    // Offer tile effects
    /**
     * Applies a hover visual effect to an offer tile slot.
     * Shows a yellow glow with a hand cursor to indicate the tile is selectable.
     *
     * @param slot the StackPane of the offer tile to apply the effect to
     */
    public static void applyOfferHoverEffect(StackPane slot) {
        slot.setStyle("-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0); -fx-cursor: hand;");
    }

    /**
     * Applies a click visual effect to an offer tile slot.
     * Shows a lime glow with a slight scale down to confirm the selection.
     *
     * @param slot the StackPane of the offer tile to apply the effect to
     */
    public static void applyOfferClickEffect(StackPane slot) {
        slot.setStyle("-fx-effect: dropshadow(gaussian, lime, 18, 0.8, 0, 0); -fx-scale-x: 0.95; -fx-scale-y: 0.95;");
    }

    /**
     * Removes any active visual effect from an offer tile slot.
     *
     * @param slot the StackPane of the offer tile to reset
     */
    public static void resetOfferEffect(StackPane slot) {
        slot.setStyle("");
    }

    // Player board

    // Skip button effects

    /**
     * Applies the hover visual effect to the skip button.
     *
     * @param button the skip button to apply the effect to
     */
    public static void applySkipHoverEffect(Button button) {
        button.setStyle(
                "-fx-background-color: #ff4400;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 6 18 6 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,80,0,0.5), 10, 0.3, 0, 2);"
        );
    }

    /**
     * Applies the default visual style to the skip button.
     * Used when the mouse exits the button to restore its normal appearance.
     *
     * @param button the skip button to reset to its default style
     */
    public static void applySkipDefaultEffect(Button button) {
        button.setStyle(
                "-fx-background-color: #cc3300;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 6 18 6 18;" +
                        "-fx-cursor: hand;"
        );
    }


    // Player board card slot effects

    /**
     * Applies a hover visual effect to a player board card slot.
     *
     * @param slot the StackPane of the player card slot to apply the effect to
     */
    public static void applyPlayerCardHoverEffect(StackPane slot) {
        slot.setEffect(new DropShadow(20, Color.BLACK));
        slot.setScaleX(1.05);
        slot.setScaleY(1.05);
    }

    /**
     * Removes any active visual effect from a player board card slot,
     * restoring its default scale and clearing any drop shadow.
     *
     * @param slot the StackPane of the player card slot to reset
     */
    public static void resetPlayerCardEffect(StackPane slot) {
        slot.setEffect(null);
        slot.setScaleX(1.0);
        slot.setScaleY(1.0);
    }

    // Overlay card effects

    /**
     * Applies a hover visual effect to a card image in the overlay view.
     *
     * @param iv the ImageView of the overlay card to apply the effect to
     */
    public static void applyOverlayCardHoverEffect(ImageView iv) {
        iv.setScaleX(1.08);
        iv.setScaleY(1.08);
        iv.setEffect(new DropShadow(15, Color.web("#FFD700")));
    }

    /**
     * Removes any active visual effect from an overlay card image,
     *
     * @param iv the ImageView of the overlay card to reset
     */
    public static void resetOverlayCardEffect(ImageView iv) {
        iv.setScaleX(1.0);
        iv.setScaleY(1.0);
        iv.setEffect(null);
    }

    // End game card effects

    //non usato per problema di duplicazione dei nickname nel database
    /**
     * Highlights the leaderboard entry belonging to the local player.
     * Applies a yellow background with rounded corners and a subtle drop shadow
     * to distinguish the player's own result from the others.
     *
     * @param container the HBox of the leaderboard entry to highlight
     */
    public static void applyMyResultHighlight(HBox container) {
        container.setStyle(
                "-fx-background-color: yellow;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0.2, 0, 2);"
        );
    }

    // Game info label styles

    /**
     * Applies the style to the round info label on the game board.
     *
     * @param label the round Label to style
     */
    public static void applyRoundLabelStyle(Label label) {
        label.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #f5e6c8;" +
                        "-fx-background-color: rgba(0,0,0,0.35);" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 4 12 4 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0.3, 0, 2);"
        );
    }

    /**
     * Applies style to the era info label on the game board.
     *
     * @param label the era Label to style
     */
    public static void applyEraLabelStyle(Label label) {
        label.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #d4a017;" +
                        "-fx-background-color: rgba(0,0,0,0.35);" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 4 12 4 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 6, 0.3, 0, 2);"
        );
    }

}