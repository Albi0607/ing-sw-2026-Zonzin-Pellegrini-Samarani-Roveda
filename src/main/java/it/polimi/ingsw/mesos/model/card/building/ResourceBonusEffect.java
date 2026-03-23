package it.polimi.ingsw.mesos.model.card.building;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.Inventor;
import it.polimi.ingsw.mesos.model.enums.*;

public class ResourceBonusEffect implements BuildingEffect {

    private final EventType eventContext;
    private final CharacterType countRef;
    private final ResourceType reward;
    private final int amount;

    public ResourceBonusEffect(EventType eventContext, CharacterType countRef,
                               ResourceType reward, int amount) {
        this.eventContext=eventContext;
        this.countRef=countRef;
        this.reward=reward;
        this.amount=amount;
    }

    @Override
    public void applyEffect(Player player, Game game, TriggerType trigger) {
        if(trigger==TriggerType.ON_CHARACTER_ADDED) {
            //condizione per gestire l'effetto 1 che da 5 di cibo per ogni volta che si completa un set di 6 character
            //controllo che il numero di carte con lo stesso tipo di quello appena aggiunto sia il numero minimo o
            // uguale rispetto agli altri
            CharacterCard lastCard = player.getTribe().getLastCard();

            if (countRef == null && reward==ResourceType.FOOD) {
                CharacterType type = lastCard.getCharacterType();
                int set = player.getTribe().countCharacters(type);
                for (CharacterType t : CharacterType.values()) {
                    int count = player.getTribe().countCharacters(t);
                    if (set > count) {
                        return;
                    }
                }
                player.addFood(amount);
            }
            //condizione per gestire l'effetto 5 prendete 3 cibo ogni volta che ottenete una coppia di inventori uguali
            if(countRef == CharacterType.INVENTOR && lastCard instanceof Inventor){
                Inventor inv = (Inventor) lastCard;
                long sameIconCount = player.getTribe().getInventors().stream()
                        .filter(i -> i.getIcon().equals(inv.getIcon()))
                        .count();
                if(sameIconCount % 2 == 0) { // ogni coppia
                    player.addFood(amount);
                }
            }
        }


        //gestione degli effetti 8 e 10 in base a il personaggio su cui agiscono
        if(trigger==TriggerType.ON_EVENT && eventContext!=null){
            if(game.getCurrentEventType()!=eventContext) return;
            switch(eventContext){
                case HUNT:
                    int num1 = player.getTribe().countCharacters(CharacterType.HUNTER);
                    player.addFood(num1*amount);
                    player.updatePrestige(num1*amount);
                    break;
                case ROCK_ART:
                    int num2 = player.getTribe().countCharacters(CharacterType.ARTIST);
                    player.addFood(num2*amount);
                    break;
            }
        }

    }
}
