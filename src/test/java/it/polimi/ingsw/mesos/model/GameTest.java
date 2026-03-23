package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void check_Ok_Nickname() {
        List<Player> name= new ArrayList<Player>();
        Player P1 = new Player("Alberto",Color.PURPLE);
        Player P2 = new Player("Anna",Color.RED);
        Player P3 = new Player("Luca",Color.BLUE);
        Player P4 = new Player("Mattia",Color.YELLOW);
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
        Player P1 = new Player("Alberto", Color.PURPLE);
        Player P2 = new Player("Anna",Color.RED);
        Player P3 = new Player("Luca",Color.BLUE);
        Player P4 = new Player("Mattia",Color.YELLOW);
        name.add(P1);
        name.add(P2);
        name.add(P3);
        name.add(P4);
        Game game= new Game(name);
        assertFalse(game.checkNicknameUnique("Alberto"));
    }
}