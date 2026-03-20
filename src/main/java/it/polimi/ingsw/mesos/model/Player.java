package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.enums.Color;

public class Player {

    private String nickname;
    private int food;
    private int prestigePoints;
    private Tribe tribe;
    private Color type;

    public Player(String nickname, String color) { }

    public void addFood(int amount) { }

    /**
     * @return false if the player does not have enough food
     */
    public int payFood(int amount) { return 0; }

    // secondo me è meglio che l'aggiornamento dei punti sia gestito da un metodo fine turno/evento che itera
    // l'update per tutti i player piuttosto che chiamare ripetutamente lo stesso metodo da istanze diverse di player
    public void updatePrestige(int points) { }


    // --- Getters ---

    public String getNickname() { return null; }

    public int getFood() { return 0; }

    public int getPrestigePoints() { return 0; }

    public Tribe getTribe() { return null; }

    public Player getPlayer() { return null; }
}
