package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.mesos.model.card.building.*;
import it.polimi.ingsw.mesos.model.card.character.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateBuildingCard {

    private List<BuildingCard> allBuildingCards;

    public CreateBuildingCard(String path) {
        loadCards(path);
    }

    private void loadCards(String path) {
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


    public List<BuildingCard> getAllBuildingCards() {
        return allBuildingCards;
    }
}