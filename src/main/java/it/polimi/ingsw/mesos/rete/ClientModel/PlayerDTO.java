package it.polimi.ingsw.mesos.rete.ClientModel;


import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;

public class PlayerDTO implements Serializable {

    public String nickname;
    public int food;
    public int prestigePoints;
    public TribeDTO tribe;
    public Color color;

}
