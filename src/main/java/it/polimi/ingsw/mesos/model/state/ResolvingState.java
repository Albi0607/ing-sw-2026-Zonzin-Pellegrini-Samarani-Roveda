package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;

public class ResolvingState implements GameStateLogic {

    /** How many players have fully resolved their offer action this round. */
    private int actionsResolvedCount;

    /**
     * How many cards the current player still needs to pick
     * before their turn is considered complete.
     */
    private int pendingCardPicks;

    public ResolvingState() { }

    /**
     * Determines resolution order (left to right on OfferTrack) and
     * waits for each player to complete their picks.
     * When all players are done, transitions to EventState.
     */
    @Override
    public void execute(Game game) { }

    /**
     * Invoked by the controller when a player picks a card from the upper row.
     * Validates it is this player's turn and they are allowed to pick from upper.
     * Adds card to tribe (CharacterCard) or checks food and purchases (BuildingCard).
     * Notifies building effects with ON_CHARACTER_ADDED or ON_PURCHASE trigger.
     * Decrements pendingCardPicks; when 0, finalizes the player's turn.
     */
    public void takeCardFromUpper(Game game, Player player, int index) { }

    /**
     * Invoked by the controller when a player picks a card from the lower row.
     * Same validation and logic as takeCardFromUpper.
     */
    public void takeCardFromLower(Game game, Player player, int index) { }

    /**
     * Invoked by the controller when a player explicitly skips a Building card
     * they cannot or do not want to afford.
     * Decrements pendingCardPicks accordingly.
     */
    public void skipBuilding(Game game, Player player, int index) { }

    /**
     * Finalizes the current player's turn:
     * - returns totem to first free slot on TurnOrderTrack
     * - awards food bonus if the slot has one
     * - charges 1 food (or 2 PP) if the slot is the last one
     * - notifies ON_TURN_ORDER_PLACEMENT building effects
     * - increments actionsResolvedCount
     */
    private void finalizePlayerTurn(Game game, Player player) { }

    /** Returns the player who should act next (leftmost totem on OfferTrack). */
    public Player getCurrentResolver(Game game) { return null; }

    public int getActionsResolvedCount() { return 0; }

    public int getPendingCardPicks() { return 0; }
}
