package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.enums.GameState;
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


        board.shiftUpperToLower();
        System.out.println("-> Personaggi scalati nella fila inferiore.");

        // 2. Calcolo dinamico del targetSize: numero giocatori + 4
        int numPlayers = g.getPlayers().size();
        int targetSize = numPlayers + 4;

        if (board.getTribeDeck() != null) {
            board.refillRows(board.getTribeDeck(), targetSize);
            System.out.println("-> Fila superiore riempita.");
        }


        System.out.println("Setup completato. Passaggio alla fase di piazzamento!");
        g.changeState(new PlacingState());
    }

    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase di setup!!!");
    }

    @Override
    public GameState getStateId() { return GameState.SETUP; }
}
