package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class EndGameScoringEffect implements BuildingEffect {

    private int pointsPerSet;
    private int fixedPoints;
    /**
     * Character type used as multiplier (e.g. HUNTER → PP per Hunter).
     * Null if the effect is not character-type-dependent.
     */
    private CharacterType multiplierRef;

    public EndGameScoringEffect(int pointsPerSet, int fixedPoints, CharacterType multiplierRef) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }
}
