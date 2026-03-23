package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.character.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateCharacterCard {

    private List<CharacterCard> allCharacterCards;

    public CreateCharacterCard(){
        loadCards();
    }

    private void loadCards(){
        try {
            List<CharacterCardJson> characterCardJsons = OpenFileJson.loadList(
                    "characters.json",
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
                                j.playersRequired
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


    public List<CharacterCard> getAllCharacterCards() {
        return allCharacterCards;
    }
}
