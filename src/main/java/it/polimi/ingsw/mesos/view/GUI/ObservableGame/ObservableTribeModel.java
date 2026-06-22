package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.common.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.common.ClientModel.TribeDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Observable model representing a player's tribe for the JavaFX GUI layer.
 * Holds the tribe's character cards and building cards as observable lists,
 * updating them only when their content actually changes to avoid
 * unnecessary UI refresh events.
 */

public class ObservableTribeModel implements DTOUpdatable<TribeDTO> {

    // Tribe card lists
    private final ObservableList<CardDTO> characters = FXCollections.observableArrayList();
    private final ObservableList<CardDTO> buildings = FXCollections.observableArrayList();

    // Observable list accessors
    public ObservableList<CardDTO> getCharacters() {
        return characters;
    }
    public ObservableList<CardDTO> getBuildings() {
        return buildings;
    }

    /**
     * Updates this tribe model from the provided TribeDTO.
     *
     * Each list is updated only if its content differs from the DTO,
     * avoiding unnecessary change events on the UI.
     *
     * @param dto the DTO containing the updated tribe data; if null,
     *            no changes are made
     */
    public void updateFromDTO(TribeDTO dto){
        if (dto == null) return;

        if (!characters.equals(dto.characters)) {
            characters.setAll(dto.characters);
        }
        if (!buildings.equals(dto.buildings)) {
            buildings.setAll(dto.buildings);
        }
    }

}
