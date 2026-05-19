package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import javafx.beans.property.*;

public class ObservablePlayerModel {

    private final StringProperty nickname = new SimpleStringProperty();

    private final IntegerProperty food = new SimpleIntegerProperty();

    private final IntegerProperty prestigePoints = new SimpleIntegerProperty();

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    private final ObservableTribeModel tribe = new ObservableTribeModel();

    //metodi getter
    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public int getFood() {
        return food.get();
    }

    public IntegerProperty foodProperty() {
        return food;
    }

    public int getPrestigePoints() {
        return prestigePoints.get();
    }

    public IntegerProperty prestigePointsProperty() {
        return prestigePoints;
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public ObservableTribeModel getTribe() {
        return tribe;
    }


    public void updateFromDTO(PlayerDTO dto) {

        if(dto==null){
            return;
        }

        nickname.set(dto.nickname);
        food.set(dto.food);
        prestigePoints.set(dto.prestigePoints);
        color.set(dto.color);

        tribe.updateFromDTO(dto.tribe);
    }

}
