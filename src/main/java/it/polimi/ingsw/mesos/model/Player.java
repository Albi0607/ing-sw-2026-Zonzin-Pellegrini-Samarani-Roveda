package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.common.enums.Color;

/**
 * Represents a player in the game.
 * Maintains the player's resources, prestige points, their tribe, and various game modifiers.
 */
public class Player {

    private final String nickname;
    private int food;
    private int prestigePoints;
    private final Tribe tribe;
    private final Color color;

    private boolean shamanNotLosePoints = false;
    private boolean shamanDoublePoints = false;
    private int extraShamanIcons = 0;
    private int sustenanceDiscount = 0;
    private boolean foodOnTotemSlot = false;
    private boolean extraDraw = false;

    /**
     * Constructs a new Player with a nickname and color.
     * Initializes food and prestige points to zero and creates a new empty tribe.
     *
     * @param nickname the player's unique identifier
     * @param color    the player's assigned color
     */
    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new Tribe();
    }

    /**
     * Adds a specified amount of food to the player's resources.
     *
     * @param amount the amount of food to add
     * @throws IllegalArgumentException if the amount is negative
     */
    public void addFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount of food!");
        }
        this.food += amount;
    }

    /**
     * Checks for food availability and updates the food count if the payment is successful.
     *
     * @param amount the amount of food to be paid
     * @return true if there is enough food to pay, false otherwise
     * @throws IllegalArgumentException if the amount is negative
     */
    public boolean payFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount of food!");
        }
        if (food >= amount) {
            this.food -= amount;
            return true;
        }
        return false;
    }

    /**
     * Updates the player's prestige points.
     *
     * @param points the points to add (or subtract if negative)
     */
    public void updatePrestige(int points) {
        this.prestigePoints += points;
    }

    /**
     * Returns the player's nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the current amount of food the player has.
     *
     * @return the food count
     */
    public int getFood() {
        return this.food;
    }

    /**
     * Returns the player's current prestige points.
     *
     * @return the prestige points
     */
    public int getPrestigePoints() {
        return this.prestigePoints;
    }

    /**
     * Returns the player's tribe.
     *
     * @return the Tribe instance
     */
    public Tribe getTribe() {
        return tribe;
    }

    /**
     * Returns the player's assigned color.
     *
     * @return the Color enum value
     */
    public Color getColor() {
        return color;
    }

    /**
     * Enables the modifier that prevents losing points from Shaman effects.
     */
    public void setShamanNotLosePoints(){
        this.shamanNotLosePoints=true;
    }

    /**
     * Enables the modifier that doubles points gained from Shaman effects.
     */
    public void setShamanDoublePoints(){
        this.shamanDoublePoints=true;
    }

    /**
     * Checks if the player is protected from losing Shaman-related points.
     *
     * @return true if the protection is active
     */
    public boolean getShamanNotLosePoints(){
        return shamanNotLosePoints;
    }

    /**
     * Checks if the player receives double points from Shamans.
     *
     * @return true if the double points modifier is active
     */
    public boolean getShamanDoublePoints(){
        return shamanDoublePoints;
    }

    /**
     * Sets the number of extra Shaman icons granted to the player.
     *
     * @param num the number of extra icons
     */
    public void setExtraShamanIcons(int num){
        extraShamanIcons=num;
    }

    /**
     * Returns the number of extra Shaman icons the player has.
     *
     * @return the number of extra icons
     */
    public int getExtraShamanIcons(){
        return extraShamanIcons;
    }

    /**
     * Sets the food discount for player sustenance.
     *
     * @param num the discount value
     */
    public void setSustenanceDiscount(int num){
        sustenanceDiscount=num;
    }

    /**
     * Returns the player's food discount for sustenance.
     *
     * @return the sustenance discount
     */
    public int getSustenanceDiscount(){
        return sustenanceDiscount;
    }

    /**
     * Enables the ability to store food on a totem slot.
     */
    public void setFoodOnTotemSlot(){
        this.foodOnTotemSlot=true;
    }

    /**
     * Checks if the player can store food on a totem slot.
     *
     * @return true if the ability is active
     */
    public boolean getFoodOnTotemSlot(){
        return foodOnTotemSlot;
    }

    /**
     * Enables the extra draw ability for the player.
     */
    public void setExtraDraw(){
        this.extraDraw=true;
    }

    /**
     * Checks if the player has an extra draw available.
     *
     * @return true if an extra draw is available
     */
    public boolean getExtraDraw(){
        return extraDraw;
    }


    public void setFood(int food){// da togliere solo per test
        this.food = food;
    }

    public void setPrestigePoints(int prestigePoints){// da togliere solo per test
        this.prestigePoints = prestigePoints;
    }

}
