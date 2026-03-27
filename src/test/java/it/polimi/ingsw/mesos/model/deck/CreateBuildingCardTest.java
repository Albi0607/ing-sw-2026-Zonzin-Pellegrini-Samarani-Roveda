package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBuildingCardTest {
    @Test
    void testCorrectNumberOfBuilding(){
        List<BuildingCard> buildingDeck = new CreateBuildingCard("building.json").getAllBuildingCards();
        assertEquals(21,buildingDeck.size());
    }

}