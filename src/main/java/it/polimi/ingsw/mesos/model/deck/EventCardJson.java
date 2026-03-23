package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;

public class EventCardJson {
    public EventType type;
    public Era era;
    public int playersRequired;
    public boolean isFinal;

    public Integer loseNumber;
    public Integer gainNumber;
    public Integer losePoints;
    public Integer gainPoints;


}
