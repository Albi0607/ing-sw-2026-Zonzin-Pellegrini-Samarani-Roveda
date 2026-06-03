/**
 * Represents a turn order track in the game.
 *
 * This class models the cards on which the totem are placed
 * to determine the turn order for each round. Each slot on a track has a specific effect,
 * such as granting or requiring resources, which is applied when a player occupies it.
 *
 * Main fields:
 * - positions: a list of players occupying each slot; a slot is null if free
 * - slots: an array of integers representing the effect of each slot
 */
package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Player;

import java.util.ArrayList;
import java.util.List;

public class TurnOrderTrack {

    private final List<Player> positions;
    private final int[] slots;
    private boolean effectsActive = false;

    /**
     * Constructs a TurnOrderTrack with the given slots.
     *
     * Each slot in the array represents the effect of that position
     * (for example, the amount of food a player gains or pays when occupying it).
     * Initially, all positions are empty (null), meaning no player occupies any slot.
     *
     * @param slots an array of integers representing the effect of each slot
     */
    public TurnOrderTrack(int[] slots) {
        this.slots = slots;
        this.positions = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            positions.add(null); // tutti gli slot vuoti
        }
    }
    /**
     * Places the given player in the specified slot and applies the slot's effect.
     *
     * The effect of the slot is determined by the value in the slots array at the given index:
     * - If the value is positive, the player gains that amount of food
     * - If the value is negative, the player pays that amount of food
     *
     * After applying the effect, the player is recorded in the positions list at the same index,
     * marking the slot as occupied.
     *
     * @param index the index of the slot where the player will be placed
     * @param p the player to occupy the slot
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void setPlayerAt(int index, Player p) {
        if (index < 0 || index >= positions.size()) {
            throw new IndexOutOfBoundsException("Index not valid: " + index);
        }
        if (positions.get(index) != null) {
            throw new IllegalStateException("Index not free: " + index);
        }
        if(effectsActive){
            int effect = slots[index];
            if (effect >= 0 && p.getFoodOnTotemSlot() == false) {
                p.addFood(effect);    // add food
            } else if (effect >= 0 && p.getFoodOnTotemSlot() == true) {
                p.addFood(effect + 1);
            } else {
                boolean paid = p.payFood(-effect);
                if (!paid) {
                    p.updatePrestige(-2);
                    // if the payer do not have enough food have to pay in prestige points
                }
            }
        }
        // update the player's position in the track
        positions.set(index, p);
    }

    /**
     * Returns the index of the first free slot in the track.
     * @return the index of the first free slot, or -1 if there are no slot available
     */
    public int getFirstFreeSlot() {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) == null) {
                return i; // first free slot
            }
        }
        throw new IllegalStateException("No free slots available"); // no free slots available
    }

    /**
     * Resets the turn order track by clearing all player positions.
     * <p>
     * After calling this method, all slots in the track will be set to {@code null},
     * making them available for a new turn order assignment.
     * Typically used at the end of a round before assigning players to new positions.
     */
    public void resetOrder() {
        for (int i = 0; i < positions.size(); i++) {
            positions.set(i, null);
        }
    }

    /**
     * Removes the given player from the track, freeing their slot.
     *
     * <p>Used when a player places their token on an offer tile and must therefore
     * vacate their current position in the turn order track. Only the slot belonging
     * to {@code p} is cleared; all other positions remain unchanged.
     *
     * @param p the {@link Player} to remove from the track
     */
    public void removePlayer(Player p) { //aggiunto qeusto metodo per eliminare i player dalla turn orderatrack ogni volta ch epiazzano un totem sulla tessera offerta
        for (int i = 0; i < positions.size(); i++) {
            if (p.equals(positions.get(i))) {
                positions.set(i, null); // Libera solo il posto di QUESTO giocatore
                return;
            }
        }
    }

    // --- Getters ---
    public Player getPlayerAt(int index) {
        if (index < 0 || index >= positions.size()) {
            throw new IndexOutOfBoundsException("Invalid slot index");
        }
        return positions.get(index);
    }

    public List<Player> getPositions() {
        return new ArrayList<>(positions); // return a copy to avoid external modification
    }

    public int[] getSlots() {
        return slots;
    }

    // --- Setters ---
    /**
     * Enables or disables the application of slot effects when players are placed.
     *
     * <p>Effects are inactive by default and should be enabled by calling
     * {@code setEffectsActive(true)} once the game has started, so that
     * pre-game token placement does not trigger food bonuses or penalties.
     *
     * @param active {@code true} to enable slot effects; {@code false} to disable them
     */
    public void setEffectsActive(boolean active) {
        this.effectsActive = active;
    }
}