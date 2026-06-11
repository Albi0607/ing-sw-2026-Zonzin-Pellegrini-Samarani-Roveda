package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import javafx.beans.property.*;

/**
 * Observable model representing a single player's state for the JavaFX GUI layer.
 * Wraps a PlayerDTO into JavaFX observable properties, allowing UI
 * components to bind directly and react automatically to player state changes.
 */
public class ObservablePlayerModel implements DTOUpdatable<PlayerDTO> {

    // Player properties
    private final StringProperty nickname = new SimpleStringProperty();
    private final IntegerProperty food = new SimpleIntegerProperty();
    private final IntegerProperty prestigePoints = new SimpleIntegerProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final ObservableTribeModel tribe = new ObservableTribeModel();


    // Observable property accessors: each method returns the JavaFX property
    // for UI binding, or the plain value via the corresponding getter
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


    /**
     * Updates this player model from the provided PlayerDTO.
     * All scalar properties are updated directly. The tribe model is updated
     * via its own updateFromDTO method, which internally checks
     * for changes before modifying its lists.
     *
     * @param dto the DTO containing the updated player data; if null, no changes are made
     */
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
