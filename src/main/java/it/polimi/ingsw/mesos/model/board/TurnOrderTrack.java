package it.polimi.ingsw.mesos.model.board;

import it.polimi.ingsw.mesos.model.Totem;

import java.util.List;

public class TurnOrderTrack {

    private List<Totem> positions;
    private int maxSlots;

    public TurnOrderTrack(int maxSlots) { }

    public void setTotemAt(int index, Totem t) { }

    public int getFirstFreeSlot() { return 0; }

    /** Replace current order with a new one (called at end of round). */
    public void updateOrder(List<Totem> newOrder) { }

    public Totem getTotemAt(int index) { return null; }

    /** Returns true if index is the last slot (costs 1 food). */
    public boolean isLastSlot(int index) { return false; }

    /** Returns food bonus associated with the slot (0 if none). */
    public int getFoodBonusAt(int index) { return 0; }

    public List<Totem> getPositions() { return null; }

    public int getMaxSlots() { return 0; }
}
