package it.polimi.ingsw.mesos.RMI.ClientModel;


import java.io.Serializable;
import java.util.List;

public class TribeDTO implements Serializable {

    public List<CardDTO> characters;
    public List<CardDTO> buildings;

}
