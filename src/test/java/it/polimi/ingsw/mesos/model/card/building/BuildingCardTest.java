package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.EventType;
import it.polimi.ingsw.mesos.common.enums.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardTest {

    @Test
    void testGetCost() {
        BuildingCard buildingCard = new BuildingCard(Era.ERA_I,10,7,new ResourceBonusEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, ResourceType.FOOD,3));
        assertEquals(10,buildingCard.getCost());
    }

    @Test
    void testGetVictoryPoints() {
        BuildingCard buildingCard = new BuildingCard(Era.ERA_I,10,7,new ResourceBonusEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, ResourceType.FOOD,3));
        assertEquals(7,buildingCard.getVictoryPoints());
    }

    @Test
    void testGetEffect() {
        BuildingCard buildingCard = new BuildingCard(Era.ERA_I,10,7,new ResourceBonusEffect(EventType.SUSTENANCE, CharacterType.INVENTOR, ResourceType.FOOD,3));
        assertInstanceOf(ResourceBonusEffect.class,buildingCard.getEffect());
    }
}