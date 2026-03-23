package it.polimi.ingsw.mesos.model.card.event;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

//attenzione all'utilizzo delle carte evento che potrebbero modificare il comportamento
/**Concrete class to handle CavePaintingEvents
 * @author Alberto Roveda
 */
public class CavePaintingEvent extends EventCard {

    /**Number of Artists that must be exceeded to avoid losing prestige points */
    private final  int loseNumber;
    /**Minimum number of Artists required to gain prestige points*/
    private final int gainNumber;
    /**Prestige points lost if the number of Artists is equal to or less than loseNumber*/
    private final int losePoints;
    /**Prestige points gain if the number of Artists is equal to or more than gainNumber*/
    private final int gainPoints;

    /**Constructor for CavePaintingEvent cards
     *
     * @param era the era of the card
     * @param playersRequired the number of players required to use the card in the game
     * @param loseNumber number of Artists that must be exceeded to avoid losing prestige points
     * @param gainNumber minimum number of Artists required to gain prestige points
     * @param losePoints prestige points lost if the number of Artists is equal to or less than loseNumber
     * @param gainPoints prestige points gain if the number of Artists is equal to or more than gainNumber
     */
    public CavePaintingEvent(Era era, int playersRequired,boolean isFinal,
                             int loseNumber, int gainNumber,
                             int losePoints, int gainPoints) {
        super(era, playersRequired, EventType.PAINTING, isFinal);
        this.loseNumber=loseNumber;
        this.gainNumber=gainNumber;
        this.losePoints=losePoints;
        this.gainPoints=gainPoints;
    }

    /**
     * Method that resolves CavePaintingEvents.
     * For each CavePaintingEvent, every player gains gainPoints for the number of Artists
     * they have in their tribe if the number of Artists is greater than or equal to gainNumber.
     * Otherwise, they lose prestige points as specified in losePoints
     * if the number of Artists is less than or equal to loseNumber.
     * @param game it is used to get the list of players whose food and prestige points will be affected
     */
    @Override
    public void resolve(Game game) {

        //faccio l'azione per tutti i giocatori in gioco
        for(Player p : game.getPlayers()){
            //conto i numeri di artisti all'interno della tribù
            int numArtists = p.getTribe().getCharactersTypeCount(CharacterType.ARTIST);

            //controllo se il giocatore ha pochi artisti e deve perdere punti prestigio
            if(numArtists<=loseNumber){
                p.updatePrestige(-losePoints);
            }

            //controllo se il giocatore ha abbastanza artisti per guadagnare punti prestigio moltiplicati per il numero di artisti
            else if(numArtists>=gainNumber){
                p.updatePrestige(numArtists*gainPoints);
            }
        }
    }
}
