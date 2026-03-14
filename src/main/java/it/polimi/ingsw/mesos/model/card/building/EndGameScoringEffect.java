package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class EndGameScoringEffect extends BuildingEffect {

    /** PP awarded per complete set (e.g. 6 PP per set of 6 different character types). */
    private int pointsPerSet;

    /** Flat PP bonus (e.g. 25 PP regardless of tribe composition). */
    private int fixedPoints;

    /**
     * Character type used as multiplier (e.g. HUNTER → PP per Hunter).
     * Null if the effect is not character-type-dependent.
     */
    private CharacterType multiplierRef;

    public EndGameScoringEffect(int pointsPerSet, int fixedPoints, CharacterType multiplierRef) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }

    public int getPointsPerSet() { return 0; }

    public int getFixedPoints() { return 0; }

    public CharacterType getMultiplierRef() { return null; }
}
