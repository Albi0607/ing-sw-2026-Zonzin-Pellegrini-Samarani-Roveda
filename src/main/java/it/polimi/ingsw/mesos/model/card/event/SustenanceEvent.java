package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;


//attenzione all'utilizzo delle carte evento che potrebbero modificare il comportamento

/**Concrete class to handle SustenanceEvents
 * @author Alberto Roveda
 */
public class SustenanceEvent extends EventCard {

    /**Attribute that indicates how many prestige points are lost for food that cannot be paid*/
    private final int prestigePoints;

    /**Constructor for SustenanceEvent cards
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param isFinal attribute to define the two final cards
     * @param prestigePoints indicates how many prestige points are lost for food that cannot be paid
     */
    public SustenanceEvent(Era era, int playersRequired, boolean isFinal, int prestigePoints) {
        super(era, playersRequired, EventType.SUSTENANCE,isFinal);
        this.prestigePoints=prestigePoints;
    }

    /**Method that resolves SustenanceEvents.
     For each SustenanceEvent, the player must pay 1 food for each character card in their tribe.
     If the player does not have enough food, they lose prestige points for each unpaid food.
     Gatherers provide a discount of 3 food that does not need to be paid, but this is handled through
     getSustenanceDiscount in the Tribe class.
     The SustenanceEvent is always the last event to be activated.
     @param game it is used to get the list of players whose food and prestige points will be affected
     */
    @Override
    public void resolve(Game game) {

        //chiamo gli edifici di tutti i giocatori che potrebbero modificare questo evento
        game.notifyBuildingEffects(TriggerType.ON_SUSTENANCE_EVENT);

        //faccio azione per tutti i giocatori in gioco
        for (Player p : game.getPlayers()){
            int food = 0;
            int missingFood = 0;

            //sommo tutte le carte giocatori contandole per tipo
            for (CharacterType c : CharacterType.values()){
                food += p.getTribe().getCharactersTypeCount(c);
            }
            //ottengo sconto dato dalle carte Gatherer e da discount presente in un attributo del giocatore
            food = food - p.getTribe().getSustenanceDiscount();
            food = food - p.getSustenanceDiscount();

            //controllo che il cibo sia positivo prima di toglierlo per non fare danni (esempio sommare cibo se diventa negativo)
            if(food>0&&food<=p.getFood()){
                p.payFood(food);
            }

            //controllo che il giocatore paghi tutto il cibo altrimenti calcolo differenza e tolgo punti prestigio
            else if(food>0&&food>p.getFood()){
                int diff = food-p.getFood();
                p.payFood(food-diff);
                p.updatePrestige(-diff*prestigePoints);
            }
            //rimetto a 0 in modo tale da fare la somma corretta nel prossimo evento
            p.setSustenanceDiscount(0);
        }
    }
}
