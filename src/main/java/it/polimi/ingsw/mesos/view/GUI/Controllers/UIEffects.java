package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.view.GUI.Controllers.Board.CardController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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
     * Applies a soft parchment wash with a faded Mesos box-art veil.
     * The art sits behind the page wash at low opacity so the ledger UI stays legible.
     *
     * @param rootPane the AnchorPane to apply the background to
     */
    public static void applyBackground(AnchorPane rootPane) {
        try {
            if (!rootPane.getStyleClass().contains("mesos-page")) {
                rootPane.getStyleClass().add("mesos-page");
            }
            Image image = new Image(UIEffects.class.getResource("/images/tool/cover.png").toExternalForm());
            ImageView background = new ImageView(image);
            background.setPreserveRatio(false);
            background.setOpacity(0.12);
            background.getStyleClass().add("mesos-art-veil");
            background.setMouseTransparent(true);
            background.fitWidthProperty().bind(rootPane.widthProperty());
            background.fitHeightProperty().bind(rootPane.heightProperty());
            rootPane.getChildren().add(0, background);
        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELL'IMMAGINE DI BACKGROUND: " + e.getMessage());
            e.printStackTrace();
            if (!rootPane.getStyleClass().contains("mesos-page")) {
                rootPane.getStyleClass().add("mesos-page");
            }
        }
    }

    /**
     * Applies the high-resolution Mesos cover art as the login screen backdrop.
     * More visible than the soft veil used on other pre-game screens.
     *
     * @param rootPane the login root pane
     */
    public static void applyLoginBackground(AnchorPane rootPane) {
        try {
            if (!rootPane.getStyleClass().contains("mesos-page")) {
                rootPane.getStyleClass().add("mesos-page");
            }
            Image image = new Image(UIEffects.class.getResource("/images/tool/cover.png").toExternalForm());
            ImageView background = new ImageView(image);
            background.setPreserveRatio(false);
            background.setOpacity(0.52);
            background.setMouseTransparent(true);
            background.fitWidthProperty().bind(rootPane.widthProperty());
            background.fitHeightProperty().bind(rootPane.heightProperty());
            rootPane.getChildren().add(0, background);
        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELLA COVER DI LOGIN: " + e.getMessage());
            e.printStackTrace();
            applyBackground(rootPane);
        }
    }

    /**
     * Adds a quiet warm atmosphere behind the game board: cover art heavily
     * blurred and desaturated so figures/title dissolve into a soft glow
     * that does not compete with cards or player boards.
     *
     * @param rootPane the game scene root
     */
    public static void applyBoardAtmosphere(AnchorPane rootPane) {
        try {
            Image image = new Image(UIEffects.class.getResource("/images/tool/cover.png").toExternalForm());
            ImageView atmosphere = new ImageView(image);
            atmosphere.setPreserveRatio(false);
            atmosphere.setOpacity(0.22);
            atmosphere.setMouseTransparent(true);
            atmosphere.fitWidthProperty().bind(rootPane.widthProperty());
            atmosphere.fitHeightProperty().bind(rootPane.heightProperty());

            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setSaturation(-0.55);
            colorAdjust.setBrightness(-0.08);
            colorAdjust.setContrast(-0.05);

            GaussianBlur blur = new GaussianBlur(34);
            blur.setInput(colorAdjust);
            atmosphere.setEffect(blur);

            rootPane.getChildren().add(0, atmosphere);
        } catch (Exception e) {
            System.err.println("ERRORE NEL CARICAMENTO DELL'ATMOSFERA BOARD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Styles a ComboBox so button cell and popup cells always use dark ink on parchment.
     * Prevents white-on-white text in the dropdown list.
     *
     * @param combo the combo box to style
     * @param <T>   item type
     */
    public static <T> void applyParchmentCombo(javafx.scene.control.ComboBox<T> combo) {
        javafx.scene.paint.Paint ink = Color.web("#2A1C12");
        javafx.util.Callback<javafx.scene.control.ListView<T>, javafx.scene.control.ListCell<T>> factory = lv ->
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                        setTextFill(ink);
                    }
                };
        combo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(combo.getPromptText());
                    setTextFill(Color.web("#6B5340"));
                } else {
                    setText(item.toString());
                    setTextFill(ink);
                }
            }
        });
        combo.setCellFactory(factory);
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
        button.getStyleClass().setAll("mesos-skip-button");
        button.setStyle(
                "-fx-background-color: #C45A22;" +
                        "-fx-text-fill: #F7F0DE;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 3;" +
                        "-fx-border-radius: 3;" +
                        "-fx-border-color: #2A1C12;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-padding: 6 18 6 18;" +
                        "-fx-cursor: hand;"
        );
    }

    /**
     * Applies the default visual style to the skip button.
     * Used when the mouse exits the button to restore its normal appearance.
     *
     * @param button the skip button to reset to its default style
     */
    public static void applySkipDefaultEffect(Button button) {
        button.getStyleClass().setAll("mesos-skip-button");
        button.setStyle(
                "-fx-background-color: #A84B1C;" +
                        "-fx-text-fill: #F7F0DE;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 3;" +
                        "-fx-border-radius: 3;" +
                        "-fx-border-color: #2A1C12;" +
                        "-fx-border-width: 1.2;" +
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
                "-fx-background-color: rgba(168,75,28,0.22);" +
                        "-fx-background-radius: 3;" +
                        "-fx-border-radius: 3;" +
                        "-fx-border-color: #A84B1C;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-padding: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(42,28,18,0.15), 8, 0.2, 0, 2);"
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
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Georgia;" +
                        "-fx-text-fill: #2A1C12;" +
                        "-fx-background-color: #F3E6C8;" +
                        "-fx-background-radius: 3;" +
                        "-fx-padding: 5 14 5 14;" +
                        "-fx-border-color: #8B6914;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 3;" +
                        "-fx-effect: dropshadow(gaussian, rgba(42,28,18,0.35), 8, 0.25, 0, 2);"
        );
    }

    /**
     * Applies style to the era info label on the game board.
     *
     * @param label the era Label to style
     */
    public static void applyEraLabelStyle(Label label) {
        label.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: Georgia;" +
                        "-fx-text-fill: #A84B1C;" +
                        "-fx-background-color: #F3E6C8;" +
                        "-fx-background-radius: 3;" +
                        "-fx-padding: 5 14 5 14;" +
                        "-fx-border-color: #8B6914;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 3;" +
                        "-fx-effect: dropshadow(gaussian, rgba(42,28,18,0.35), 8, 0.25, 0, 2);"
        );
    }

}