package it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System;

public class EventCardAction implements CardAction{

    @Override
    public void onClickCard(CardController c) {
        // feedback “non cliccabile”
        c.setClickEffect("-fx-effect: dropshadow(gaussian, red, 15, 0.9, 0, 0); -fx-opacity: 0.8;");
    }

    @Override
    public void onCardEnter(CardController c) {
        c.setHoverEffect("-fx-effect: dropshadow(gaussian, darkred, 12, 0.6, 0, 0); -fx-cursor: not-allowed;");
    }

    @Override
    public void onCardExit(CardController c) {
        c.resetEffect();
    }
}
