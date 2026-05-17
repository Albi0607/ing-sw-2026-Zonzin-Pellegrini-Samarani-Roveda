package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.TribeDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ObservableTribeModel {


    private final ObservableList<CardDTO> characters = FXCollections.observableArrayList();

    private final ObservableList<CardDTO> buildings = FXCollections.observableArrayList();

    //metodi getter
    public ObservableList<CardDTO> getCharacters() {
        return characters;
    }

    public ObservableList<CardDTO> getBuildings() {
        return buildings;
    }

    public void updateFromDTO(TribeDTO dto){
        characters.setAll(dto.characters);

        buildings.setAll(dto.buildings);

    }
}
