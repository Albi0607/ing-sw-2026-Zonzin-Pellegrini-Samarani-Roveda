package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.enums.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    @DisplayName("Constructor")
    void constructor_test() {
        Player P1 = new Player("Alberto", Color.BLUE);
        assertEquals("Alberto", P1.getNickname());
        assertEquals(Color.BLUE, P1.getColor());
        assertEquals(0, P1.getFood());
        assertEquals(0, P1.getPrestigePoints());
        assertNotNull(P1.getTribe());
    }

    @Nested
    @DisplayName("addFood() and payFood()")
    class add_pay_Food {

        @Test
        @DisplayName("food positive increment")
        void addFood_Normal() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.addFood(1);
            assertEquals(1, P1.getFood());
        }

        @Test
        @DisplayName("food negative increment exception")
        void addFood_Exception() {
            Player P1 = new Player("Alberto", Color.BLUE);
            assertThrows(IllegalArgumentException.class, () -> P1.addFood(-1));
        }

        @Test
        @DisplayName("food positive decrement")
        void payFood_Normal() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.addFood(2);
            assertTrue(P1.payFood(2));
        }

        @Test
        @DisplayName("food rest after decrement")
        void payFood_Rest() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.addFood(4);
            P1.payFood(2);
            assertEquals(2, P1.getFood());
        }

        @Test
        @DisplayName("food amount not enough decrement")
        void payFood_Not_Enough() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.addFood(1);
            assertFalse(P1.payFood(2));
        }

        @Test
        @DisplayName("food negative decrement exception")
        void payFood_Exception() {
            Player P1 = new Player("Alberto", Color.BLUE);
            assertThrows(IllegalArgumentException.class, () -> P1.payFood(-1));
        }
    }
    @Nested
    @DisplayName("updatePrestige()")
    class updatePrestigePoints {

        @Test
        @DisplayName("prestige points positive increment")
        void addPrestige_Normal() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.updatePrestige(25);
            assertEquals(25, P1.getPrestigePoints());
        }

        @Test
        @DisplayName("prestige points negative increment")
        void addPrestige_Negative() {
            Player P1 = new Player("Alberto", Color.BLUE);
            P1.updatePrestige(-25);
            assertEquals(-25, P1.getPrestigePoints());
        }
    }


}