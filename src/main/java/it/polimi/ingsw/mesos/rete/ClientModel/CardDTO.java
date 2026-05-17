package it.polimi.ingsw.mesos.rete.ClientModel;

import java.io.Serializable;
import java.util.Objects;

public class CardDTO implements Serializable {

    public String id;

    //implementazione di equals per poter evitare aggiornamenti inutili in GUI
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardDTO cardDTO = (CardDTO) o;
        return Objects.equals(id, cardDTO.id);
    }

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
