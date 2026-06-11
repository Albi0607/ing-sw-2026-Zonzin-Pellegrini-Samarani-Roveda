package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.rete.ClientModel.TurnOrderSlotDTO;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * Controller for the turn order track on the game board.
 * Displays the turn order track image based on the number of players,
 * and renders each player's totem at the correct position in the track.
 * Totems are stacked vertically.
 */
public class TurnOrderController {

    // FXML components
    @FXML StackPane turnOrderTrack;
    @FXML ImageView turnOrderTrackImage;
    @FXML Pane totemContainer;

    /**
     * Loads and displays the turn order track image based on the number of players.
     * Should be called once at the start of the game.
     *
     * @param turnOrderTrack the list of turn order slots, used to determine the player count
     */
    public void setTurnOrder(ObservableList<TurnOrderSlotDTO> turnOrderTrack){
        String path="/images/TurnOrderTrack/"+turnOrderTrack.size()+".png";
        try{
            Image image = new Image(Objects.requireNonNull(TurnOrderController.class.getResourceAsStream(path)));
            turnOrderTrackImage.setImage(image);
        }catch(Exception e){
            System.out.println("ERRORE NEL CARICAMENTO DEL TURNORDERTRACK: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the totem positions on the turn order track.
     * Clears all existing totem images and redraws them based on the current slot occupants.
     * Empty slots are skipped. Totems are stacked vertically.
     *
     * @param turnOrderTrack the updated list of turn order slots with occupant colors
     */
    public void update(ObservableList<TurnOrderSlotDTO> turnOrderTrack){

        //tolgo i vecchi totem
        totemContainer.getChildren().clear();

        double startX = 27;
        double startY = 5;
        if(turnOrderTrack.size()>=4){
            startY = -15;
        }
        double spacingY = 25;

        for(int i = 0; i < turnOrderTrack.size(); i++){

            TurnOrderSlotDTO slot = turnOrderTrack.get(i);

            //se lo slot è vuoto lo salto
            if(slot.occupantColor == null){
                continue;
            }

            String totemPath = "/images/totem/" + slot.occupantColor + ".png";

            try{

                ImageView totem = new ImageView(new Image(Objects.requireNonNull(
                        TurnOrderController.class.getResourceAsStream(totemPath))));

                totem.setFitWidth(30);
                totem.setFitHeight(45);
                totem.setPreserveRatio(true);

                //posiziono i totem
                totem.setLayoutX(startX);
                totem.setLayoutY(startY + (i * spacingY));

                totemContainer.getChildren().add(totem);

            } catch(Exception e){
                System.err.println("ERRORE NEL CARICAMENTO DEI TOTEM: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

}
