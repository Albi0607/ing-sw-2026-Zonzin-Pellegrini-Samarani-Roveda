package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.TriggerType;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.state.GameStateLogic;
// in teoria l'attributo e i metodi dovrebbero essere enum (GameState) e non l'interfaccia, no? per ora li ho sostituiti
import it.polimi.ingsw.mesos.model.enums.GameState;

import java.util.List;
import java.util.Map;

public class Game {

    private List<Player> players;
    private Board board;
    private Deck<TribeCard> tribeDeck;
    private Map<Era, Deck<BuildingCard>> buildingDecks;
    private int currentRound;
    private Era currentEra;
    private GameState currentState;

    public Game(List<Player> players) { }

    public void startGame() { }

    public void changeState(GameState newState) { }

    public boolean checkNicknameUnique(String name) { return false; }

    public void placeTotemOnOffer(Player p, OfferTile t) { }

    public void handleEraTransition(Card newCard) { }

    public void notifyBuildingEffects(TriggerType trigger, Object context) { }

    public Player getWinner() { return null; }

    // --- Getters ---

    public List<Player> getPlayers() { return null; }

    public Board getBoard() { return null; }

    public Deck<TribeCard> getTribeDeck() { return null; }

    public Map<Era, Deck<BuildingCard>> getBuildingDecks() { return null; }

    public int getCurrentRound() { return 0; }

    public Era getCurrentEra() { return null; }

    public GameState getCurrentState() { return null; }
}
