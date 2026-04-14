package it.polimi.ingsw.mesos.RMI.ClientModel;


import java.io.Serializable;

public class PlayerDTO implements Serializable {

    public String nickname;
    public int food;
    public int prestigePoints;
    public TribeDTO tribe;
    public String color;

}
