package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Player;

import java.util.List;

public class TurnOrderTrack {

    private List<Player> positions;
    private int maxSlots;

    public TurnOrderTrack(int maxSlots) { }

    public void setPlayerAt(int index, Player p) { }

    public int getFirstFreeSlot() { return 0; }

    /** Replace current order with a new one (called at end of round). */
    public void updateOrder(List<Player> newOrder) { }

//    ~ getters ~

    public Player getPlayerAt(int index) { return null; }

    public boolean isLastSlot(int index) { return false; }

    public int getFoodBonusAt(int index) { return 0; }

    public List<Player> getPositions() { return null; }

    public int getMaxSlots() { return 0; }
}
