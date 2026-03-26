package it.polimi.ingsw.mesos.model.deck;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.enums.Era;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class BuildingDeckStrategy implements CreateStrategy<BuildingCard>{


    @Override
    public Stack<BuildingCard> createDeck(int numPlayers) {
        int num1,num2,num3;
        List<BuildingCard> buildingDeckCreated = new CreateBuildingCard("building.json").getAllBuildingCards();
        List<BuildingCard> tempBuildingDeck = new ArrayList<>();
        Stack<BuildingCard> buildingDeck = new Stack<>();
        if(numPlayers==2){
            num1=1;
            num2=1;
            num3=3;
        }
        else if(numPlayers==3){
            num1=2;
            num2=2;
            num3=4;
        }
        else if(numPlayers==4){
            num1=2;
            num2=3;
            num3=4;
        }
        else if(numPlayers==5){
            num1=2;
            num2=3;
            num3=5;
        }
        else{
            //gestire errore per giocatori sbagliati
            throw new IllegalArgumentException("Numero di giocatori non valido");
        }
        tempBuildingDeck.addAll(randomCard(filterByEra(buildingDeckCreated,Era.ERA_III),num3));
        tempBuildingDeck.addAll(randomCard(filterByEra(buildingDeckCreated,Era.ERA_II),num2));
        tempBuildingDeck.addAll(randomCard(filterByEra(buildingDeckCreated,Era.ERA_I),num1));

        buildingDeck.addAll(tempBuildingDeck);
        return buildingDeck;
    }

    public List <BuildingCard> randomCard(List<BuildingCard> list, int num){
        List<BuildingCard> returnList = new ArrayList<>();
        Collections.shuffle(list);
        for(int i=0;i<num;i++){
            returnList.add(list.get(i));
        }
        return returnList;
    }

    public List <BuildingCard> filterByEra(List<BuildingCard> list, Era era){
        List<BuildingCard> eraCards = new ArrayList<>();
        for(BuildingCard b : list){
            if(b.getEra()==era){
                eraCards.add(b);
            }
        }
        return eraCards;
    }
}
