package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class EventModifierEffect extends BuildingEffect {

    /** The event this modifier applies to. */
    private EventType eventContext;

    /** Food discount during Sustenance (0 if not applicable). */
    private int discount;

    /** Virtual shaman icons added during Shamanic Ritual (0 if not applicable). */
    private int virtualIcons;

    /** If true, double PP are gained during Shamanic Ritual majority. */
    private boolean doublePrestige;

    public EventModifierEffect(EventType eventContext, int discount,
                               int virtualIcons, boolean doublePrestige) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }

    public EventType getEventContext() { return null; }

    public int getDiscount() { return 0; }

    public int getVirtualIcons() { return 0; }

    public boolean isDoublePrestige() { return false; }
}
