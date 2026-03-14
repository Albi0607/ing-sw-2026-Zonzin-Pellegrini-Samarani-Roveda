package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.ResourceType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class ResourceBonusEffect extends BuildingEffect {

    /** The event during which this effect fires (null = fires on any trigger). */
    private EventType eventContext;

    /** The character type used as multiplier (e.g. HUNTER, ARTIST). */
    private CharacterType countRef;

    /** Whether the reward is food or prestige points. */
    private ResourceType reward;

    /** Amount of resource gained per matching character. */
    private int amount;

    public ResourceBonusEffect(EventType eventContext, CharacterType countRef,
                               ResourceType reward, int amount) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }
}
