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
        int i = slots[index];
        if (i >= 0) {
            p.addFood(i);   // add food
        } else {
            p.payFood(i);
            // pay food (capire in base a come è implementato pay food se mettere + o - i
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
        return -1; // no free slots available
    }
    /**
     * Replaces the current player order with a new order.
     * Typically called at the end of a round to update the turn order.
     *
     * @param newOrder a list of players representing the new turn order
     * @throws IllegalArgumentException if newOrder size does not match the number of slots
     */
    public void updateOrder(List<Player> newOrder) {
        if (newOrder.size() != positions.size()) {
            throw new IllegalArgumentException("New order size must match number of slots");
        }
        for (int i = 0; i < positions.size(); i++) {
            positions.set(i, newOrder.get(i));
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

    // --- Setters ---
public void setSlot(int index, int value) {
    if (index < 0 || index >= slots.length) {
        throw new IndexOutOfBoundsException("Invalid slot index");
    }
    slots[index] = value;
}
}