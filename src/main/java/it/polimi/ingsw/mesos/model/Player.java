package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Color;

public class Player {

    private final String nickname;
    private int food;
    private int prestigePoints;
    private final Tribe tribe;
    private final Color color;

    //parametri che servono per gli edifici
    private boolean shamanNotLosePoints = false;
    private boolean shamanDoublePoints = false;
    private int extraShamanIcons = 0;
    private int sustenanceDiscount = 0;
    private boolean foodOnTotemSlot = false;
    private boolean extraDraw = false;

    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.food = 0;
        this.prestigePoints = 0;
        this.tribe = new Tribe();
    }

    public void addFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount of food!");
        }
        this.food += amount;
    }

    /**
     * checks for food availability on a payment action
     * @param amount the amount of food to be paid
     * @return true if there is enough amount of food to pay, food attribute is updated
     * otherwise, false if there is not enough amount, food attribute still
     */
    // il caso in cui food < amount va gestito direttamente sulla chiamata del metodo payfood ad ogni check
    // della disponibilitò durante l'evento sostentamento (tenendo conto dello sconto dato dai raccoglitori)
    // e durante l'acquisto di edifici (tenendo conto dello sconto dato dai costruttori)
    // per ora l'alternativa più semplice per chi implementerà queste cose è ritornare un booleano che
    // mi dice se il player può in quell'azione permettersi di pagare, se falso, food rimane invariato,
    // non dovrebbe andare in contrassto con la dinamica dell'evento sostentamento perchè l'azione converrà iterarla
    // per ogni personaggio
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

    public void updatePrestige(int points) {
        this.prestigePoints += points;
    }
    // --- Getters ---

    public String getNickname() {
        return this.nickname;
    }

    public int getFood() {
        return this.food;
    }

    public int getPrestigePoints() {
        return this.prestigePoints;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public Color getColor() {
        return color;
    }
    public Player getPlayer() { return null; }

    //metodi che servono per i building
    public void setShamanNotLosePoints(){
        this.shamanNotLosePoints=true;
    }
    public void setShamanDoublePoints(){
        this.shamanDoublePoints=true;
    }
    public boolean getShamanNotLosePoints(){return shamanNotLosePoints;}
    public boolean getShamanDoublePoints(){
        return shamanDoublePoints;
    }
    public void setExtraShamanIcons(int num){extraShamanIcons=num;}
    public int getExtraShamanIcons(){
        return extraShamanIcons;
    }

    public void setSustenanceDiscount(int num){sustenanceDiscount=num;}
    public int getSustenanceDiscount(){
        return sustenanceDiscount;
    }

    public void setFoodOnTotemSlot(){this.foodOnTotemSlot=true;}
    public boolean getFoodOnTotemSlot(){return foodOnTotemSlot;}
    public void setExtraDraw(){this.extraDraw=true;}
    public boolean getExtraDraw(){return extraDraw;}
}
