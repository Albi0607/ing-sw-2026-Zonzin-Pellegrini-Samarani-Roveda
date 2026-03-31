package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.enums.*;

/**Java DTO class that maps the attributes of the buildings.json file to determine which type of effect to construct and,
 *  consequently, the entire building that depends on that effect, assigning the corresponding parameters for each effect type
 *  @author Alberto Roveda
 *  */

public class BuildingCardJson {
    public Era era;
    public int cost;
    public int victoryPoints;
    public String effect;

    //ResourceBonusEffect
    public EventType eventContext;
    public CharacterType countRef;
    public ResourceType reward;
    public Integer amount;

    //EventModifierEffect
    public Integer discount;
    public Integer virtualIcons;
    public Boolean doublePrestige;
    public Boolean noLosePrestige;

    //EndGameScoringEffect
    public Integer pointsPerSet;
    public Integer prestigePoints;
    public Boolean doubleBuilderPoints;
    public CharacterType multiplierRef;

    //SpecialActionEffect
    public SpecialActionType specialType;
}

