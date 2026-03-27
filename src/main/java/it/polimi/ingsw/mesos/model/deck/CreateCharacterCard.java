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
                }

                if(card != null){
                    allCharacterCards.add(card);
                }
            }

        } catch(IOException e){
            System.err.println("Error loading cards: " + e.getMessage());
        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**Method to return to the caller the list containing all the created cards
     * @return list with all cards*/
    public List<CharacterCard> getAllCharacterCards() {
        return allCharacterCards;
    }
}
