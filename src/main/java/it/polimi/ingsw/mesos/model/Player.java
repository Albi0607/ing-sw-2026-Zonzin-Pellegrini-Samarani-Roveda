package it.polimi.ingsw.mesos.model;

public class Player {

    private String nickname;
    private int food;
    private int prestigePoints;
    private Tribe tribe;
    private Totem totem;

    public Player(String nickname, String color) { }

    public void addFood(int amount) { }

    /**
     * @return false if the player does not have enough food
     */
    public boolean payFood(int amount) { return false; }

    public void addPrestige(int points) { }

    // --- Getters ---

    public String getNickname() { return null; }

    public int getFood() { return 0; }

    public int getPrestigePoints() { return 0; }

    public Tribe getTribe() { return null; }

    public Totem getTotem() { return null; }
}
