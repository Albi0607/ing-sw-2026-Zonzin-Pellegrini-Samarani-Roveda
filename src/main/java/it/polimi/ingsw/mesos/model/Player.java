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
    /* meglio forse che il metodo sia int cosi ritorna quanto cibo manca da pagare e cosi da sapere quanti punti
    prestigio dover perdere di conseguenza?->alberto*/
    public boolean payFood(int amount) { return false; }
    /*meglio forse avere un altro metodo per perdere punti prestigio o averne uno unico che si chiama con un modo
    diverso per non confondere?->alberto*/
    public void addPrestige(int points) { }

    // --- Getters ---

    public String getNickname() { return null; }

    public int getFood() { return 0; }

    public int getPrestigePoints() { return 0; }

    public Tribe getTribe() { return null; }

    public Totem getTotem() { return null; }
}
