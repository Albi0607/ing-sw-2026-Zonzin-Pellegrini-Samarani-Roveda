package it.polimi.ingsw.mesos.common;

import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.InventionIcon;

/** Java class of type DTO that maps the attributes of the character.json file in order to determine which type of
 * character to construct, with the corresponding parameters that differ from one character to another
 * @author Alberto Roveda*/
public class CharacterCardJson {
    public String id;
    public CharacterType type;
    public Era era;
    public int playersRequired;

    //optional
    public Integer discountValue;
    public Integer prestigePoints;
    public Boolean hasIcon;
    public InventionIcon icon;
    public Integer numberOfIcons;
}
