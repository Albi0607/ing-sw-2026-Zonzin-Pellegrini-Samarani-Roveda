package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.building.*;
import it.polimi.ingsw.mesos.model.card.character.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**Class that allows creating all building cards for the Mesos game using the appropriate JSON file
 * @author Alberto Roveda
 */

public class CreateBuildingCard {

    /**List containing all building cards*/
    private List<BuildingCard> allBuildingCards;

    /**Constructor of the class that creates all the cards from a JSON file, using BuildingCardJson as a helper class
     *  to distinguish the creation of different building cards
     *  @param path file JSON in resources with all building card
     * @throws
     *  */
    public CreateBuildingCard(String path) {
        try {
            List<BuildingCardJson> buildingCardJsons = OpenFileJson.loadList(
                    path,
                    new TypeReference<List<BuildingCardJson>>() {
                    }
            );

            allBuildingCards = new ArrayList<>();

            for (BuildingCardJson j : buildingCardJsons) {
                BuildingEffect effect;
                switch (j.effect) {
                    case "ResourceBonusEffect":
                        effect = new ResourceBonusEffect(
                                j.eventContext,
                                j.countRef,
                                j.reward,
                                j.amount
                        );
                        break;

                    case "EventModifierEffect":
                        effect = new EventModifierEffect(
                                j.eventContext,
                                j.countRef,
                                j.discount,
                                j.virtualIcons,
                                j.doublePrestige,
                                j.noLosePrestige
                        );
                        break;

                    case "EndGameScoringEffect":
                        effect = new EndGameScoringEffect(
                                j.pointsPerSet,
                                j.prestigePoints,
                                j.doubleBuilderPoints,
                                j.multiplierRef
                        );
                        break;

                    case "SpecialActionEffect":
                        effect = new SpecialActionEffect(
                                j.specialType
                        );
                        break;

                    default:
                        throw new IllegalArgumentException( "Unknow building effect " + j.effect);

                }
                    BuildingCard card = new BuildingCard(
                            j.era,
                            j.cost,
                            j.victoryPoints,
                            effect
                    );

                    card.setId(j.id);

                    allBuildingCards.add(card);

                    CardRegistry.registerCard(j.id, j);//aggiunge la carta anche al nostro database



            }
            //mi devo ricordare di gestire queste eccezioni con ramo try catch quando verrà creato il deck in main
        } catch(IOException e){
            throw new RuntimeException("Error loading cards",e);
        }
    }

    /**Method to return to the caller the list containing all the created cards
     * @return list with all cards*/
    public List<BuildingCard> getAllBuildingCards() {
        return allBuildingCards;
    }
}