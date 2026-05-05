package it.polimi.ingsw.mesos.common;

import it.polimi.ingsw.mesos.common.enums.Era;
import it.polimi.ingsw.mesos.common.enums.EventType;
/**Java class of type DTO that maps the attributes of the events.json file to determine which type of event to construct and
 * assign the corresponding parameters required for different event types
 * @author Alberto Roveda*/

public class EventCardJson {
    public String id;
    public EventType type;
    public Era era;
    public int playersRequired;
    public boolean isFinal;

    public Integer loseNumber;
    public Integer gainNumber;
    public Integer losePoints;
    public Integer gainPoints;
}
