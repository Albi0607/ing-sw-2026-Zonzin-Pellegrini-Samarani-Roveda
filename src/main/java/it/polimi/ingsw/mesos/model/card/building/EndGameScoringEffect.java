package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.Builder;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.TriggerType;
/**"Concrete class that handles the building effects:
 * 9:At the end of the game, gain double the Prestige Points indicated on your Builder cards.
 * 11:At the end of the game, gain 6 Prestige Points for each set of 6 character cards of different types.
 * 12:At the end of the game, gain the indicated Prestige Points for each character card of the specified type.
 * 14:At the end of the game, gain 25 Prestige Points.
 * @author Alberto Roveda
 */
public class EndGameScoringEffect implements BuildingEffect {
    /**Prestige points gained for completed sets*/
    private final int pointsPerSet;
    /**Prestige points to be gained at the end of the game*/
    private final int prestigePoints;
    /**Attribute that defines whether to double the prestige points gained from Builders in the player's tribe*/
    private final boolean doubleBuilderPoints;
    /**Character type used as multiplier (e.g. HUNTER → PP per Hunter)*/
    private final CharacterType multiplierRef;

    /**Constructor for the effect of the 4 defined building types
     *
     * @param pointsPerSet prestige points gained for completed sets
     * @param prestigePoints prestige points to be gained at the end of the game
     * @param doubleBuildingPoints defines whether to double the prestige points gained from Builders in the player's tribe
     * @param multiplierRef character type used as multiplier
     */
    public EndGameScoringEffect(int pointsPerSet, int prestigePoints, boolean doubleBuildingPoints, CharacterType multiplierRef) {
        this.pointsPerSet = pointsPerSet;
        this.prestigePoints = prestigePoints;
        this.doubleBuilderPoints = doubleBuildingPoints;
        this.multiplierRef = multiplierRef;
    }

    /**Override of the interface method to handle the effects of the buildings
     * @param player the player who benefits from the effect
     * @param game the game on which the building's effect acts
     * @param trigger the moment when the effect is triggered, to be evaluated with the TriggerType of the invoked building
     */
    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if (trigger == TriggerType.END_GAME) {
            /**Handles effect 14, which grants 25 prestige points at the end of the game*/
            if (prestigePoints == 25) {
                player.updatePrestige(25);
            }
            /**Handles effect 11, which grants 6 prestige points at the end of the game for each completed set of 6 character cards*/
            if (pointsPerSet > 0) {
                int minSet = 100;
                for (CharacterType type : CharacterType.values()) {
                    int count = player.getTribe().getCharactersTypeCount(type);
                    if (minSet > count) {
                        minSet = count;
                    }
                }
                player.updatePrestige(minSet*pointsPerSet);
            }
            /**Handles effect 12, which grants the indicated prestige points multiplied by the number of character cards
             * of the specified type*/
            if (multiplierRef != null) {
                int count = player.getTribe().getCharactersTypeCount(multiplierRef);
                player.updatePrestige(count * prestigePoints);
            }
            /**Handles effect 9, which doubles the prestige points indicated on Builder cards in the tribe*/
            if (doubleBuilderPoints) {
                int totalBuilerPoints = 0;
                for (CharacterCard card : player.getTribe().getCharacters()) {
                    if(card instanceof Builder){
                        totalBuilerPoints += ((Builder) card).getPrestigePoints();
                    }
                }
                player.updatePrestige(totalBuilerPoints);
            }
        }
    }
}
