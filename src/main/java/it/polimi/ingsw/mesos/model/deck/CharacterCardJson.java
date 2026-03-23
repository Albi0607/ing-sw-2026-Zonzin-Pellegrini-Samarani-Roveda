package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.InventionIcon;

public class CharacterCardJson {
    public CharacterType type;
    public Era era;
    public int playersRequired;
    //opzionali
    public Integer discountValue;
    public Integer prestigePoints;
    public Boolean hasIcon;
    public InventionIcon icon;
    public Integer numberOfIcons;
}
