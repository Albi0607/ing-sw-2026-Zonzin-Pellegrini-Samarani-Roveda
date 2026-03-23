package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

public class EventModifierEffect implements BuildingEffect {

    private final EventType eventContext;
    private final CharacterType countRef;
    private final int discount;
    /** Virtual shaman icons added during Shamanic Ritual (0 if not applicable). usat per avere piu shaman icons */
    private final int virtualIcons;
    /** If true, double PP are gained during Shamanic Ritual majority. */
    private final boolean doublePrestige;
    //se vero attiva l'effetto che non si perdono punti prestigio se si hanno meno icone shamano
    private final boolean noLosePrestige;


    public EventModifierEffect(EventType eventContext,CharacterType countRef, int discount,
                               int virtualIcons, boolean doublePrestige,boolean noLosePrestige) {
        this.eventContext=eventContext;
        this.countRef=countRef;
        this.discount=discount;
        this.virtualIcons=virtualIcons;
        this.doublePrestige=doublePrestige;
        this.noLosePrestige=noLosePrestige;
    }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if (trigger==TriggerType.ON_PURCHASE){
            //gestice effetto 7 che raddoppi i punti prestigio ottenuti se si hanno più icone shamano
            if (doublePrestige){
                player.setShamanDoublePoints();
            }
            //gestisce effetto 3 che non ti fa pagare punti prestigio se hai meno shamani rispetto agli altri
            if(noLosePrestige){
                player.setShamanNotLosePoints();
            }
            //gestisce effetto 6 che ti aggiunge 3 icone shamano
            if(virtualIcons>0){
                player.setExtraShamanIcons(virtualIcons);
            }
        }
        //gestisce effetto 2 che ti dà sconto di cibo durante evento di sostentamento per numero di character di un
        //determinato tipo nella propria tribù
        if(trigger==TriggerType.ON_EVENT&&game.getCurrentEventType()==eventContext&&discount>0){
            int calculatedDiscount = discount*player.getTribe().countCharacters(countRef);
            player.setSustenanceDiscount(calculatedDiscount);
        }
    }
}
