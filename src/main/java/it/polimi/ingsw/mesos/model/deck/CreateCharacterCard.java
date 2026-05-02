package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.character.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**Class that allows creating all character cards for the Mesos game using the appropriate JSON file
 * @author Alberto Roveda
 */
public class CreateCharacterCard {

    /**List containing all character cards*/
    private List<CharacterCard> allCharacterCards;

    /**Constructor of the class that creates all the cards from a JSON file, using CharacterCardJson as a helper class
     *  to distinguish the creation of different character cards
     *  @param path file JSON in resources with all character card
     *  */
    public CreateCharacterCard(String path){
        try {
            List<CharacterCardJson> characterCardJsons = OpenFileJson.loadList(
                    path,
                    new TypeReference<List<CharacterCardJson>>() {}
            );

            allCharacterCards = new ArrayList<>();

            for(CharacterCardJson j : characterCardJsons){
                CharacterCard card = null;
                switch(j.type){
                    case BUILDER:
                        card = new Builder(
                                j.era,
                                j.playersRequired,
                                j.discountValue,
                                j.prestigePoints
                        );
                        break;

                    case GATHERER:
                        card = new Gatherer(
                                j.era,
                                j.playersRequired,
                                j.discountValue
                        );
                        break;

                    case HUNTER:
                        card = new Hunter(
                                j.era,
                                j.playersRequired,
                                j.hasIcon
                        );
                        break;

                    case SHAMAN:
                        card = new Shaman(
                                j.era,
                                j.playersRequired,
                                j.numberOfIcons
                        );
                        break;

                    case INVENTOR:
                        card = new Inventor(
                                j.era,
                                j.playersRequired,
                                j.icon
                        );
                        break;

                    case ARTIST:
                        card = new Artist(
                                j.era,
                                j.playersRequired
                        );
                        break;

                    default:
                        throw new IllegalArgumentException( "Unknow character type " + j.type);

                }

                card.setId(j.id);

                allCharacterCards.add(card);

            }
            //mi devo ricordare di gestire queste eccezioni con ramo try catch quando verrà creato il deck in main
        } catch(IOException e){
            throw new RuntimeException("Error loading cards",e);
        }
    }

    /**Method to return to the caller the list containing all the created cards
     * @return list with all cards*/
    public List<CharacterCard> getAllCharacterCards() {
        return allCharacterCards;
    }
}
