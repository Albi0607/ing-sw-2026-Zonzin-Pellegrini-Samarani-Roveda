package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single game move saved to disk.
 *
 * This class encapsulates all attributes that are relevant for various move types.
 * Unused payloads for a specific move type are left uninitialized or at their default values.
 */
public class GameMove implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of supported move types.
     */
    public enum MoveType {
        SET_NUM_PLAYERS,
        ADD_PLAYER,
        START_GAME,
        PLACE_TOTEM,
        TAKE_CARD,
        SKIP_EXTRA_DRAW
    }

    public final MoveType type;
    public final String nickname;
    public final int intPayload;
    public final char charPayload;
    public final boolean boolPayload;
    public final Color colorPayload;

    private GameMove(MoveType type, String nickname, int intPayload, char charPayload, boolean boolPayload, Color colorPayload) {
        this.type = type;
        this.nickname = nickname;
        this.intPayload = intPayload;
        this.charPayload = charPayload;
        this.boolPayload = boolPayload;
        this.colorPayload = colorPayload;
    }

    /**
     * Creates a move to set the number of players.
     *
     * @param numPlayers the number of players
     * @return a new GameMove instance
     */
    public static GameMove setNumPlayers(int numPlayers) {
        return new GameMove(MoveType.SET_NUM_PLAYERS,null,  numPlayers, '\0', false, null);
    }

    /**
     * Creates a move to add a player.
     *
     * @param nickname the player's nickname
     * @param color    the player's color
     * @return a new GameMove instance
     */
    public static GameMove addPlayer(String nickname, Color color) {
        return new GameMove(MoveType.ADD_PLAYER, nickname,0, '\0', false, color);
    }

    /**
     * Creates a move to start the game.
     *
     * @return a new GameMove instance
     */
    public static GameMove startGame() {
        return new GameMove(MoveType.START_GAME, null, 0, '\0', false, null);
    }

    /**
     * Creates a move to place a totem on a tile.
     *
     * @param nickname the player's nickname
     * @param tileId   the identifier of the tile
     * @return a new GameMove instance
     */
    public static GameMove placeTotem(String nickname, char tileId) {
        return new GameMove(MoveType.PLACE_TOTEM, nickname, 0, tileId, false, null);
    }

    /**
     * Creates a move to take a card.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the index of the card on the board
     * @param isUpper   true if the card is from the upper row, false otherwise
     * @return a new GameMove instance
     */
    public static GameMove takeCard(String nickname, int cardIndex, boolean isUpper) {
        return new GameMove(MoveType.TAKE_CARD, nickname, cardIndex, '\0', isUpper, null);
    }

    /**
     * Creates a move to skip an extra draw.
     *
     * @param nickname the player's nickname
     * @return a new GameMove instance
     */
    public static GameMove skipExtraDraw(String nickname) {
        return new GameMove(MoveType.SKIP_EXTRA_DRAW, nickname, 0, '\0', false, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return switch (type) {
            case SET_NUM_PLAYERS -> "SET_NUM_PLAYERS(" + intPayload + ")";
            case ADD_PLAYER -> "ADD_PLAYER(" + nickname + ", color=" + colorPayload + ")";
            case START_GAME -> "START_GAME";
            case PLACE_TOTEM -> "PLACE_TOTEM(" + nickname + ", " + charPayload + ")";
            case TAKE_CARD -> "TAKE_CARD(" + nickname + ", " + intPayload + ", upper=" + boolPayload + ")";
            case SKIP_EXTRA_DRAW -> "SKIP_EXTRA_DRAW(" + nickname + ")";
        };
    }
}