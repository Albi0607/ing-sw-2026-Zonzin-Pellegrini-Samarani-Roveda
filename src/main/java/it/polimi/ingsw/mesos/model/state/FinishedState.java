package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.Builder;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.common.enums.TriggerType;

/**
 * Represents the terminal phase of the game.
 * <p>
 * In this state, the game has officially concluded (usually after Era III).
 * The system calculates the final scores, applies any end-game effects
 * (such as {@link }), and determines the winner.
 * No further gameplay actions can be taken by the players.
 * </p>
 */

public class FinishedState implements GameStateLogic {

    /**
     * Executes the final scoring and game termination logic.
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {
        System.out.println("--- Entering FINISHED PHASE ---");
        g.notifyBuildingEffects(TriggerType.END_GAME);
        System.out.println("The game is over! Calculating final scores...");

        for (Player p : g.getPlayers()) {
            // Calcoliamo i bonus dalla Tribù
            int bonusPoints = calculateFinalPrestige(p.getTribe());

            // Aggiungiamo i punti al totale del giocatore
            p.updatePrestige(bonusPoints);

            System.out.println("Giocatore " + p.getNickname() + ": +" + bonusPoints + " PP calcolati.");
        }

        // Determiniamo il vincitore
        Player winner = g.getWinner();

        if (winner != null) {
            System.out.println("\n IL VINCITORE È: " + winner.getNickname() + "!");
            System.out.println("Punti totali: " + winner.getPrestigePoints());
            System.out.println("Cibo rimanente: " + winner.getFood());
        }

    }

    private int calculateFinalPrestige(Tribe tribe) {
        int points = 0;

        // 1. PP dei Costruttori
        // Sommiamo i punti stampati su ogni carta Builder presente
        points += tribe.getCharacters().stream()
                .filter(c -> c.getType() == CharacterType.BUILDER)
                .map(c -> (Builder) c)
                .mapToInt(Builder::getPrestigePoints)
                .sum();

        // 2. PP degli Inventori
        // Formula: (Numero Inventori) * (Numero icone invenzione distinte)
        int numInventors = tribe.getInventors().size();
        long distinctIcons = tribe.getDistinctInventionCount();
        points += (int) (numInventors * distinctIcons);

        // 3. PP degli Artisti (10 PP ogni 2 Artisti)
        int numArtists = tribe.getCharactersTypeCount(CharacterType.ARTIST);
        points += (numArtists / 2) * 10;

        // 4. PP degli Edifici (Punti base + Effetti di fine partita)
        for (BuildingCard b : tribe.getBuildings()) {
            // Punti base stampati sulla carta edificio
            points += b.getVictoryPoints();
        }

        return points;
    }


    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante questa fase!!!");
    }

    @Override
    public void takeCard(Game g, Player p, int cardIndex, boolean isUpper) {
        throw new IllegalStateException("Errore: Non puoi pescare carte durante la Fase di fine gioco!!!");
    }

    @Override
    public void skipExtraDraw(Game g) {
        throw new IllegalStateException("Errore: Non puoi saltare la pesca durante la Fase di fine gioco!!!");
    }

    @Override
    public GameState getStateId() { return GameState.FINISHED; }
}
