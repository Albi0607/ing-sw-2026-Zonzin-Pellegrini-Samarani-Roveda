package it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System;

public class BuildingCardAction implements CardAction{
    @Override
    public void onClickCard(CardController c) {
        c.setClickEffect("-fx-effect: dropshadow(gaussian, lime, 18, 0.8, 0, 0); -fx-scale-x: 0.95; -fx-scale-y: 0.95;");
    }

    @Override
    public void onCardEnter(CardController c) {
        c.setHoverEffect("-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0); -fx-cursor: hand;");
    }

    @Override
    public void onCardExit(CardController c) {
        c.resetEffect();
    }
}
