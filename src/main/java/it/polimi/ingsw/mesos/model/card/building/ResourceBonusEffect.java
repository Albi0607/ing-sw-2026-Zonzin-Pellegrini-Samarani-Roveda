package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.Inventor;
import it.polimi.ingsw.mesos.model.enums.*;


/**"Concrete class that handles the building effects:

 * 1:From the moment you acquire this building, gain 5 food each time you complete a set of 6 character cards of different
 * types. Do not gain food for sets that were already completed at the time of acquiring the building.
 * 5:From the moment you acquire this building, gain 3 food each time you obtain a pair of identical Inventors
 * (i.e., with the same invention icon). Do not gain food for pairs that were already owned at the time of acquiring the building.
 * 8:During the Hunt Event, for each Hunter in your tribe, gain 1 food and 1 additional prestige point.
 * 10:During the Cave Painting Event, gain 1 food for each Artist in your tribe."
 * @author  Alberto Roveda
 * */
public class ResourceBonusEffect implements BuildingEffect {
    /**event in which to activate the effect*/
    private final EventType eventContext;
    /**The type of character that multiplies the points during the effect*/
    private final CharacterType countRef;
    /**Type of resource to be gained*/
    private final ResourceType reward;
    /**Amount of resource to be gained*/
    private final int amount;

    /**Constructor for the effect of the 4 defined building types
     *
     * @param eventContext event in which to activate the effect
     * @param countRef the type of character that multiplies the points during the effect
     * @param reward type of resource to be gained
     * @param amount amount of resource to be gained
     */
    public ResourceBonusEffect(EventType eventContext, CharacterType countRef,
                               ResourceType reward, int amount) {
        this.eventContext=eventContext;
        this.countRef=countRef;
        this.reward=reward;
        this.amount=amount;
    }


    /**Override of the interface method to handle the effects of the buildings
     * @param player the player who benefits from the effect
     * @param game the game on which the building's effect acts
     * @param trigger the moment when the effect is triggered, to be evaluated with the TriggerType of the invoked building
     */
    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if(trigger==TriggerType.ON_CHARACTER_ADDED) {
            //condizione per gestire l'effetto 1 che da 5 di cibo per ogni volta che si completa un set di 6 character
            //controllo che il numero di carte con lo stesso tipo di quello appena aggiunto sia il numero minimo o
            // uguale rispetto agli altri
            /**gets the last card added to the tribe*/
            CharacterCard lastCard = player.getTribe().getLastCard();

            /**Condition to handle effect 1, which gives 5 food each time a set of 6 character cards is completed
             * Check that the number of cards of the same type as the one just added is the minimum
             * or equal compared to the other types*/
            if (countRef == null && reward==ResourceType.FOOD) {
                CharacterType type = lastCard.getCharacterType();
                int set = player.getTribe().getCharactersTypeCount(type);
                for (CharacterType t : CharacterType.values()) {
                    int count = player.getTribe().getCharactersTypeCount(t);
                    if (set > count) {
                        return;
                    }
                }
                player.addFood(amount);
            }

            /**Condition to handle effect 5: gain 3 food each time a pair of identical Inventors is obtained*/
            if(countRef == CharacterType.INVENTOR && lastCard instanceof Inventor){
                Inventor inv = (Inventor) lastCard;
                long sameIconCount = player.getTribe().getInventors().stream()
                        .filter(i -> i.getIcon().equals(inv.getIcon()))
                        .count();
                if(sameIconCount % 2 == 0) { // ogni coppia
                    player.addFood(amount);
                }
            }
        }

        /**Processing effects 8 and 10 according to the targeted character*/
        if(trigger==TriggerType.ON_EVENT && eventContext!=null){
            if(game.getCurrentEventType()!=eventContext) return;
            switch(eventContext){
                case HUNT:
                    /**During the Hunt event, gain 1 food and 1 additional prestige point*/
                    int num1 = player.getTribe().getCharactersTypeCount(CharacterType.HUNTER);
                    player.addFood(num1*amount);
                    player.updatePrestige(num1*amount);
                    break;
                case PAINTING:
                    /**During the CavePaintingEvent, gain 1 food for each Artist in your tribe*/
                    int num2 = player.getTribe().getCharactersTypeCount(CharacterType.ARTIST);
                    player.addFood(num2*amount);
                    break;
            }
        }

    }
}
