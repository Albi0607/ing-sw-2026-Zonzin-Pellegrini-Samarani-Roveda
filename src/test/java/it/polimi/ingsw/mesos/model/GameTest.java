package it.polimi.ingsw.mesos.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void check_Ok_Nickname() {
        List<Player> name= new ArrayList<Player>();
        Player P1 = new Player("Alberto","Black");
        Player P2 = new Player("Anna","Red");
        Player P3 = new Player("Luca","Blue");
        Player P4 = new Player("Mattia","Yellow");
        name.add(P1);
        name.add(P2);
        name.add(P3);
        name.add(P4);
        Game game= new Game(name);
        assertTrue(game.checkNicknameUnique("Mario"));
    }

    @Test
    void check_Wrong_Nickname() {
        List<Player> name= new ArrayList<Player>();
        Player P1 = new Player("Alberto","Black");
        Player P2 = new Player("Anna","Red");
        Player P3 = new Player("Luca","Blue");
        Player P4 = new Player("Mattia","Yellow");
        name.add(P1);
        name.add(P2);
        name.add(P3);
        name.add(P4);
        Game game= new Game(name);
        assertFalse(game.checkNicknameUnique("Alberto"));
    }
}