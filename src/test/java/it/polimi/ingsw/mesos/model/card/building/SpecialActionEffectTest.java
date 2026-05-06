package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.TriggerType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.mesos.common.enums.SpecialActionType.EXTRA_DRAW;
import static it.polimi.ingsw.mesos.common.enums.SpecialActionType.FOOD_ON_TOTEM_SLOT;
import static org.junit.jupiter.api.Assertions.*;

class SpecialActionEffectTest {

    @Test
    void testApplyEffectBuilding5() {
        Player p1 = new Player("Alberto", Color.BLUE);
        Player p2 = new Player("Anna", Color.PURPLE);
        Player p3 = new Player("Luca", Color.WHITE);
        Player p4 = new Player("Mattia", Color.RED);
        List<Player> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        Game game = new Game(list);
        BuildingCard building = new BuildingCard(Era.ERA_I,3,3,new SpecialActionEffect(FOOD_ON_TOTEM_SLOT));

        building.getEffect().applyEffect(p1,game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3,game, TriggerType.ON_PURCHASE);

        assertTrue(p1.getFoodOnTotemSlot());
        assertFalse(p1.getExtraDraw());

        assertFalse(p2.getFoodOnTotemSlot());
        assertFalse(p2.getExtraDraw());

        assertTrue(p3.getFoodOnTotemSlot());
        assertFalse(p3.getExtraDraw());

        assertFalse(p4.getFoodOnTotemSlot());
        assertFalse(p4.getExtraDraw());
    }

    @Test
    void testApplyEffectBuilding20() {
        Player p1 = new Player("Alberto", Color.BLUE);
        Player p2 = new Player("Anna", Color.PURPLE);
        Player p3 = new Player("Luca", Color.WHITE);
        Player p4 = new Player("Mattia", Color.RED);
        List<Player> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        Game game = new Game(list);
        BuildingCard building = new BuildingCard(Era.ERA_III,9,3,new SpecialActionEffect(EXTRA_DRAW));

        building.getEffect().applyEffect(p1,game, TriggerType.ON_PURCHASE);
        building.getEffect().applyEffect(p3,game, TriggerType.ON_PURCHASE);

        assertTrue(p1.getExtraDraw());
        assertFalse(p1.getFoodOnTotemSlot());

        assertFalse(p2.getExtraDraw());
        assertFalse(p2.getFoodOnTotemSlot());

        assertTrue(p3.getExtraDraw());
        assertFalse(p3.getFoodOnTotemSlot());

        assertFalse(p4.getExtraDraw());
        assertFalse(p4.getFoodOnTotemSlot());
    }
}