package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.ResourceType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class ResourceBonusEffect implements BuildingEffect {

    private EventType eventContext;
    private CharacterType countRef;
    private ResourceType reward;
    private int amount;

    public ResourceBonusEffect(EventType eventContext, CharacterType countRef,
                               ResourceType reward, int amount) { }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) { }
}
