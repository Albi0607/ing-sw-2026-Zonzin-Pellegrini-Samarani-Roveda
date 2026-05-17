package it.polimi.ingsw.mesos.view.GUI.Card_Rendering_System;


public interface CardAction {
    void onClickCard(CardController cardController);
    void onCardEnter(CardController cardController);
    void onCardExit(CardController cardController);
}
