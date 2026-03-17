package it.polimi.ingsw.mesos.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void addFood_Normal() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(1);
        assertEquals(1,P1.getFood());
    }

    @Test
    void addFood_Negative() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(-1);
        assertEquals(0,P1.getFood());
    }

    @Test
    void addFood_Zero() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(0);
        assertEquals(0,P1.getFood());
    }

    @Test
    void payFood_Normal() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(2);
        assertEquals(0,P1.payFood(2));
    }

    @Test
    void payFood_Rest() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(4);
        P1.payFood(2);
        assertEquals(2,P1.getFood());
    }

    @Test
    void payFood_Not_Enough() {
        Player P1 = new Player("Alberto","Black");
        P1.addFood(1);
        assertEquals(0,P1.payFood(2));
    }

    @Test
    void addPrestige_Normal() {
        Player P1 = new Player("Alberto","Black");
        P1.updatePrestige(25);
        assertEquals(25,P1.getPrestigePoints());
    }

    @Test
    void addPrestige_Negative() {
        Player P1 = new Player("Alberto","Black");
        P1.updatePrestige(-25);
        assertEquals(-25,P1.getPrestigePoints());
    }


}