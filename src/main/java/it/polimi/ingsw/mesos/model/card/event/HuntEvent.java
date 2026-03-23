package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

//attenzione all'utilizzo delle carte evento che potrebbero modificare il comportamento
/**Concrete class to handle HunteEvents
 * @author Alberto Roveda
 */
public class HuntEvent extends EventCard {

    /**Attribute that defines the prestige points to be gained for each Hunter in the player's tribe*/
    private final int prestigePoints;

    /**Constructor for HuntEvent cards
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param prestigePoints defines the prestige points to be gained for each Hunter in the player's tribe
     */
    public HuntEvent(Era era, int playersRequired, boolean isFinal, int prestigePoints) {
        super(era, playersRequired, EventType.HUNT, isFinal);
        this.prestigePoints=prestigePoints;
    }

    /**Method that resolves HuntEvents
     * For each HuntEvent, players gain 1 food for each Hunter in their tribe.
     * They also gain prestige points as specified by prestigePoints
     * multiplied by the number of Hunters in their tribe.
     * @param game it is used to get the list of players whose food and prestige points will be affected
     */
    @Override
    public void resolve(Game game) {

        for(Player p : game.getPlayers()){
            int numHunters = p.getTribe().getCharactersTypeCount(CharacterType.HUNTER);
            p.addFood(numHunters);
            p.updatePrestige(numHunters*prestigePoints);
        }
    }
}
