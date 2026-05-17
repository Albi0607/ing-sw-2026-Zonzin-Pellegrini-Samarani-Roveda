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
    public void update(ObservableList<TurnOrderSlotDTO> turnOrderTrack){
        path="/images/TurnOrderTrack/"+turnOrderTrack.size()+".png";
        try{
            Image image = new Image(Objects.requireNonNull(TurnOrderController.class.getResourceAsStream(path)));
            turnOrderTrackImage.setImage(image);
        }catch(Exception e){
            System.err.println("ERRORE NEL CARICAMENTO DEL TURNORDERTRACK: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
