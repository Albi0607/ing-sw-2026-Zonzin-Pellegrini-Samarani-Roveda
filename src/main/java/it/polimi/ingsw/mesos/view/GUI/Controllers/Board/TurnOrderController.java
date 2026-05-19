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

public class TurnOrderController {

    @FXML StackPane turnOrderTrack;
    @FXML ImageView turnOrderTrackImage;
    @FXML Pane totemContainer;
    private String path;
    private ObservableGameModel gameModel;

    //disegnare il turnOrderTrack una sola volta in set e in questo metodo cambiare solo la posizione dei totem
    public void setTurnOrder(ObservableList<TurnOrderSlotDTO> turnOrderTrack){
        path="/images/TurnOrderTrack/"+turnOrderTrack.size()+".png";
        try{
            Image image = new Image(Objects.requireNonNull(TurnOrderController.class.getResourceAsStream(path)));
            turnOrderTrackImage.setImage(image);
        }catch(Exception e){
            System.err.println("ERRORE NEL CARICAMENTO DEL TURNORDERTRACK: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(ObservableList<TurnOrderSlotDTO> turnOrderTrack){

        //tolgo i vecchi totem
        totemContainer.getChildren().clear();

        double startX = 30;
        double startY = 5;
        if(turnOrderTrack.size()>=4){
            startY = -5;
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

                totem.setFitWidth(25);
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
