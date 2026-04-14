package it.polimi.ingsw.mesos.RMI.ClientModel;

import java.io.Serializable;

public class CardDTO implements Serializable {

    public int id;
    //se carta event o carta character o carta edificio
    public String type;
    //se è una carta evento distingui il tipo
    public String eventType;
    //se è una carta personaggio distingui il tipo
    public String characterType;

    public int era;

    //non serve utilizzo id della carta per visionarla
/*
    //attributi degli eventi
    public int positivePP;
    public int negativePP;
    public int numArtBonus;
    public int numArtMalus;

    //attributi per carte personaggio
    public String Invention;
    //dato che è sempre 3 si potrebbe anche fare senza (nel model è gestito un numero generico per estendibilità)
    public int food;

    public int stars;

    public int discount;
    public int prestigePoints;

    public boolean icon;
    */

}
