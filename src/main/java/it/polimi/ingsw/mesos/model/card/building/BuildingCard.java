package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.common.enums.Era;

/**"Generic class for building cards"
 * @author Alberto Roveda
 */
public class BuildingCard extends Card {
    /**Attribute for the cost of the building card*/
    private final int cost;
    /**Attribute that identifies the prestige points to be gained at the end of the game*/
    private final int victoryPoints;
    /**Attribute that identifies the effect class of the building*/
    private final BuildingEffect effect;

    /**Constructor for building cards
     *
     * @param era the era of the card
     * @param cost the cost of the building card
     * @param victoryPoints identifies the prestige points to be gained at the end of the game
     * @param effect identifies the effect class of the building
     */
    public BuildingCard(Era era, int cost, int victoryPoints, BuildingEffect effect) {
        super(era);
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.effect = effect;
    }

    // --- Getters ---

    /**Getter method that returns the cost of the building to be paid for purchase
     *
     * @return the cost of the building card
     */
    @Override
    public int getCost() {
        return this.cost;
    }

    /**Getter method that returns the prestige points gained from the building at the end of the game
     *
     * @return the prestige points to be gained at the end of the game
     */
    public int getVictoryPoints() {
        return this.victoryPoints;
    }

    /**Getter method that @returns the effect class of the building*/
    public BuildingEffect getEffect() {
        return this.effect;
    }

    @Override
    public void addTo(Player player) {
        player.getTribe().addBuilding(this); // Chiama il tuo metodo specifico
    }

}
