package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class EndGameScoringEffect implements BuildingEffect {

    private final int pointsPerSet;
    private final int prestigePoints;
    private final boolean doubleBuildingPoints;
    /**
     * Character type used as multiplier (e.g. HUNTER → PP per Hunter).
     * Null if the effect is not character-type-dependent.
     */
    private final CharacterType multiplierRef;

    public EndGameScoringEffect(int pointsPerSet, int prestigePoints, boolean doubleBuildingPoints, CharacterType multiplierRef) {
        this.pointsPerSet = pointsPerSet;
        this.prestigePoints = prestigePoints;
        this.doubleBuildingPoints = doubleBuildingPoints;
        this.multiplierRef = multiplierRef;
    }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if (trigger == TriggerType.END_GAME) {
            if (prestigePoints == 25) {
                player.updatePrestige(25);
            }
            if (pointsPerSet > 0) {
                int minSet = 100;
                for (CharacterType type : CharacterType.values()) {
                    int count = player.getTribe().countCharacters(type);
                    if (minSet > count) {
                        minSet = count;
                    }
                }
                player.updatePrestige(minSet*pointsPerSet);
            }
            if (multiplierRef != null) {
                int count = player.getTribe().countCharacters(multiplierRef);
                player.updatePrestige(count * prestigePoints);
            }

            if (doubleBuildingPoints) {
                int totalBuildingPoints = 0;
                for (BuildingCard building : player.getTribe().getBuildingCard()) {
                    totalBuildingPoints += building.getVictoryPoints();
                }
                player.updatePrestige(totalBuildingPoints);
            }
        }
    }
}
