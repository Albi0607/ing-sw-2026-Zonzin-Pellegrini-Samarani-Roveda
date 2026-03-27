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
                BuildingEffect effect = null;
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

                }
                if (effect != null) {
                    BuildingCard card = new BuildingCard(
                            j.era,
                            j.cost,
                            j.victoryPoints,
                            effect
                    );
                    allBuildingCards.add(card);

                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cards: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**Method to return to the caller the list containing all the created cards
     * @return list with all cards*/
    public List<BuildingCard> getAllBuildingCards() {
        return allBuildingCards;
    }
}