package it.polimi.ingsw.mesos.view.GUI.Controllers.PlayerBoard;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.CardView;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;


import java.util.List;

/**
 * Utility class responsible for rendering and updating a single card slot
 * on a player board panel.
 * Handles image loading, count label updates, hover effects and click behavior
 * to open the card overlay.
 */
public class PlayerCardView {

    /**
     * Updates a player board card slot with a newly acquired card.
     * Makes the slot visible if it was hidden, increments the card count label,
     * renders the card image, and sets up hover and click handlers.
     * On click, opens the CardOverlayView showing all cards of the same type.
     *
     * @param id the id of the card to display
     * @param stackPane the StackPane slot to update
     * @param imageView the ImageView inside the slot to render the card image into
     * @param label the Label showing the count of cards of this type
     * @param cardIds the full list of card ids of this type, used for the overlay
     */
    public static void addNewCard(String id, StackPane stackPane, ImageView imageView, Label label, List<String> cardIds) {

        if (!stackPane.isVisible()) {
            stackPane.setVisible(true);
            stackPane.setManaged(true);
        }

        String current = label.getText();
        int count = (current == null || current.isEmpty()) ? 0 : Integer.parseInt(current);
        count++;
        label.setText(String.valueOf(count));
        label.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: rgba(0,0,0,0.70);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 1 4 1 4;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0.5, 0, 0);"
        );

        CardDTO dto = new CardDTO();
        dto.id=id;
        CardView.render(imageView, dto);

        // hover
        stackPane.setOnMouseEntered(e -> UIEffects.applyPlayerCardHoverEffect(stackPane));
        stackPane.setOnMouseExited(e -> UIEffects.resetPlayerCardEffect(stackPane));

        // click — apre overlay con tutte le carte della tipologia
        if (cardIds != null && !cardIds.isEmpty()) {
            stackPane.setOnMouseClicked(e -> CardOverlayView.show(cardIds));
        }
    }

}

