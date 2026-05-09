package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateBuildingCardTest {
    @Test
    void testCorrectNumberOfBuilding(){
        List<BuildingCard> buildingDeck = new CreateBuildingCard("cards/buildings.json").getAllBuildingCards();
        assertEquals(21,buildingDeck.size());
    }

    @Test
    void testWrongInputFromJson(){
        assertThrows(IllegalArgumentException.class,()->new CreateBuildingCard("cards/wrongBuildingsForTest.json"));
    }
    @Test
    void testWrongFile(){
        assertThrows(RuntimeException.class,()->new CreateBuildingCard("prova.json"));
    }

}