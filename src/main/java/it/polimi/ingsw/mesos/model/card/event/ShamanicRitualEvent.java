package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

//attenzione all'utilizzo delle carte evento che potrebbero modificare il comportamento
//attenzione a regola dell'edificio sui doppi punti per più giocatori con stessi punti shamano
//attenzione a regola dell'edificio sui punti non persi per più giocatori con stessi punti shamano
/**Concrete class to handle ShamanicRitualEvent
 * @author Alberto Roveda
 */
public class ShamanicRitualEvent extends EventCard {
    /**Prestige points gained if the player has the most Shaman icons*/
    private final int gainPrestige;
    /**Prestige points lost if the player has the fewest Shaman icons*/
    private final int losePrestige;
    //per la carta edificio che non ti fa perdere punti se hai meno icone shamano degli altri giocatori
    /**Boolean attribute, default false, to indicate whether the corresponding building card has been purchased,
     * which prevents losing prestige points if the player has the fewest Shaman icons*/
    private boolean notLosePoints=false;
    //per la carta edificio che ti raddoppia i punti prestigio guadagnati se hai piu shamani degli altri giocatori
    /**Boolean attribute, default false, to indicate whether the corresponding building card has been purchased,
     * which allows the player to gain double prestige points if they have the most Shaman icons*/
    private boolean doublePoints=false;

    /**Constructor for ShamanicRitualEvent cards
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param isFinal attribute to define the two final cards
     * @param gainPrestige Prestige points gained if the player has the most Shaman icons
     * @param losePrestige Prestige points lost if the player has the fewest Shaman icons
     */
    public ShamanicRitualEvent(Era era, int playersRequired, boolean isFinal, int gainPrestige, int losePrestige) {
        super(era, 2, EventType.SHAMAN_RITUAL,isFinal);
        this.gainPrestige=gainPrestige;
        this.losePrestige=losePrestige;
    }

    //da utilizzare alla chiamata della carta edificio corrispondente
    /**Setter method to be used when the specific building is purchased,
     * which prevents losing prestige points if the player has the fewest Shaman icons*/
    public void setNotLosePoints() {
        this.notLosePoints = true;
    }

    //da utilizzare alla chiamata della carta edificio corrispondente
    /**Setter method to be used when the building is purchased,
     * which allows doubling the prestige points gained if the player has the most Shaman icons*/
    public void setDoublePoints() {
        doublePoints = true;
    }




    /**
     * method that resolve ShamanicRitualEvents
     * For each ShamanicRitualEvent, the player with the most Shaman icons gains the prestige points specified by
     * gainPrestige; the player with the fewest Shaman icons loses the prestige points specified by losePrestige.
     * In case of a tie between multiple players for the most or fewest Shaman icons,
     * all tied players gain or lose the corresponding prestige points.
     * In the exceptional case where all players are tied, each player first gains
     * and then loses the corresponding prestige points.
     * @param game it is used to get the list of players whose food and prestige points will be affected
     */
    @Override
    public void resolve(Game game) {

        int max=0;
        int min=100;
        for(Player p : game.getPlayers()){
            int icons = p.getTribe().getTotalShamanIcons();
            if(icons>max){
                max=icons;
            }
            if(icons<min){
                min=icons;
            }
        }

        //gestisce il caso in cui tutti i giocatori hanno stessi punti shamano
        if(max==min){
            for(Player p : game.getPlayers()){
                if(doublePoints){
                    p.updatePrestige(gainPrestige*2);
                }
                else {
                    p.updatePrestige(gainPrestige);
                }
                if(!notLosePoints) {
                    p.updatePrestige(-losePrestige);
                }
            }
        }
        else{
            for(Player p : game.getPlayers()){
                int icons = p.getTribe().getTotalShamanIcons();
                if(icons==max){
                    if(doublePoints){
                        p.updatePrestige(gainPrestige*2);
                    }
                    else {
                        p.updatePrestige(gainPrestige);
                    }
                }
                else if(icons==min){
                    if(!notLosePoints) {
                        p.updatePrestige(-losePrestige);
                    }
                }
            }
        }


    }
}

