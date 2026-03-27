package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
/**Java class of type DTO that maps the attributes of the event.json file to determine which type of event to construct and
 * assign the corresponding parameters required for different event types
 * @author Alberto Roveda*/

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
