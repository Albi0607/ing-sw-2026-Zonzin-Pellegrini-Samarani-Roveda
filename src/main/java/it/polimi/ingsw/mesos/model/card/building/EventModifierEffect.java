package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.EventType;
import it.polimi.ingsw.mesos.common.enums.TriggerType;
/**Concrete class that handles the building effects:

 * 2:During the Sustenance Event, you receive a discount of 1 food on the total you would have to pay for each character
 * card of the specified type in your tribe (Artists / Inventors / Gatherers).
 * 3:During the Shamanic Ritual Event, you do not lose prestige points if you have fewer Shaman icons than the other players.
 * 6:During the Shamanic Ritual Event, your tribe gains 3 additional Shaman icons.
 * 7:During the Shamanic Ritual Event, if you have more Shaman icons than all other players, you gain double the specified prestige points.
 * If you gain prestige points together with other players (because you have the same number of icons), you do not gain the double.
 * @author ALberto Roveda
 */
public class EventModifierEffect implements BuildingEffect {

    /**event in which to activate the effect*/
    private final EventType eventContext;
    /**The type of character that multiplies the points during the effect*/
    private final CharacterType countRef;
    /**Discount obtained during the Sustenance event*/
    private final int discount;
    /** Virtual shaman icons added during Shamanic Ritual*/
    private final int virtualIcons;
    /** If true, double prestigePoints are gained during Shamanic Ritual majority.*/
    private final boolean doublePrestige;
    /**If true, activates the effect that prevents losing prestige points when having fewer Shaman icons*/
    private final boolean noLosePrestige;

    /**Constructor for the effect of the 4 defined building types
     *
     * @param eventContext event in which to activate the effect
     * @param countRef the type of character that multiplies the points during the effect
     * @param discount discount obtained during the Sustenance event
     * @param virtualIcons virtual shaman icons added during Shamanic Ritual
     * @param doublePrestige double prestigePoints are gained during Shamanic Ritual majority
     * @param noLosePrestige activates the effect that prevents losing prestige points when having fewer Shaman icons
     */
    public EventModifierEffect(EventType eventContext,CharacterType countRef, int discount,
                               int virtualIcons, boolean doublePrestige,boolean noLosePrestige) {
        this.eventContext=eventContext;
        this.countRef=countRef;
        this.discount=discount;
        this.virtualIcons=virtualIcons;
        this.doublePrestige=doublePrestige;
        this.noLosePrestige=noLosePrestige;
    }

    /**Override of the interface method to handle the effects of the buildings
     * @param player the player who benefits from the effect
     * @param game the game on which the building's effect acts
     * @param trigger the moment when the effect is triggered, to be evaluated with the TriggerType of the invoked building
     */
    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if (trigger==TriggerType.ON_PURCHASE){
            /**Handles effect 7, which doubles the prestige points gained if the player has the most Shaman icons*/
            if (doublePrestige){
                player.setShamanDoublePoints();
            }
            /**Handles effect 3, which prevents losing prestige points if you have fewer Shaman icons than the other players*/
            if(noLosePrestige){
                player.setShamanNotLosePoints();
            }
            /**Handles effect 6, which adds 3 Shaman icons to your tribe*/
            if(virtualIcons>0){
                player.setExtraShamanIcons(virtualIcons);
            }
        }
            /**Handles effect 2, which grants a food discount during the Sustenance event based on the number of
             * characters of a specific type in your tribe*/
        if(trigger==TriggerType.ON_SUSTENANCE_EVENT&&eventContext==EventType.SUSTENANCE&&discount>0){
            int calculatedDiscount = discount*player.getTribe().getCharactersTypeCount(countRef);
            player.setSustenanceDiscount(player.getSustenanceDiscount()+calculatedDiscount);
        }
    }
}
