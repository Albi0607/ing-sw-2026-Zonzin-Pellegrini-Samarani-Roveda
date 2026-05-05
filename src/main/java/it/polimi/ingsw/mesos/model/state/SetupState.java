package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.common.enums.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the setup phase of a single round in the game.
 * <p>
 * This state is responsible for preparing the board for the current round,
 * such as refilling the offer track with new cards from the decks.
 * It does NOT handle the initial game setup (which is managed by Game.startGame()).
 * Once the round is set up, it automatically transitions the game
 * to the {@link PlacingState}.
 * </p>
 */
public class SetupState implements GameStateLogic {

    /**
     * Executes the round setup logic.
     * <p>
     * Refills the board rows and prepares the players' totems if necessary.
     * Immediately transitions to the placing phase upon completion.
     * </p>
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {

        System.out.println("--- Entering SETUP PHASE ---");
        System.out.println("Preparing the board for the current round...");

        Board board = g.getBoard();
        int n = g.getPlayers().size();
        int targetSize = n + 4;

        List<Card> lowerBuildings = new ArrayList<>();
        for (Card c : board.getLowerRow()) {
            if (c instanceof BuildingCard) {
                lowerBuildings.add(c);
            }
        }

        board.clearLowerRow();

        board.shiftUpperToLower();

        board.getLowerRow().addAll(lowerBuildings);

        board.refillRows(targetSize, g);
        g.setCurrentRound(g.getCurrentRound()+1);

        int nextTrackIndex = 0;
        List<OfferTile> tiles = board.getTiles();
        TurnOrderTrack turnOrderTrack = board.getTurnOrderTrack();

        turnOrderTrack.resetOrder();

        for (OfferTile tile : tiles) {
            Player playerOnTile = tile.getHost();

            if (playerOnTile != null) {
                System.out.println("DEBUG: Trovato " + playerOnTile.getNickname() + " sulla tessera " + tile.getId());
                turnOrderTrack.setPlayerAt(nextTrackIndex, playerOnTile);
                nextTrackIndex++;

                tile.reset();
            }
        }

        System.out.println("Board pronta. Passaggio al piazzamento totem.");

        g.changeState(new PlacingState());

    }


    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase di setup!!!");
    }

    @Override
    public void takeCard(Game g, Player p, int cardIndex, boolean isUpper) {
        throw new IllegalStateException("Errore: Non puoi pescare carte durante la Fase di setup!!!");
    }

    @Override
    public void skipExtraDraw(Game g) {
        throw new IllegalStateException("Errore: Non puoi saltare la pesca durante la Fase di setup!!!");
    }

    @Override
    public GameState getStateId() { return GameState.SETUP; }
}
