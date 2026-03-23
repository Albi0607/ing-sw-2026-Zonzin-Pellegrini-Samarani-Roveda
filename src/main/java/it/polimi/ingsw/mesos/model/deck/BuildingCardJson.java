package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.enums.*;

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
    public Boolean doubleBuildingPoints;
    public CharacterType multiplierRef;

    //SpecialActionEffect
    public SpecialActionType specialType;
}

