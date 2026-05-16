package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.common.enums.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una singola mossa di gioco, salvata su disco.
 *
 * Permette di serializzare le diverse mosse in un unico oggetto, il quale raggruppa tutti
 * gli attributi di ciascuna, i campi irrilevanti non vengono inizializzati.
 */
public class GameMove implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum MoveType {
        SET_NUM_PLAYERS,
        ADD_PLAYER,
        START_GAME,
        PLACE_TOTEM,
        TAKE_CARD,
        SKIP_EXTRA_DRAW
    }

    public final MoveType type;

    // Campi payload — solo quelli rilevanti per il tipo vengono riempiti.
    public final String  nickname;
    public final int     intPayload;    // numPlayers oppure cardIndex
    public final char    charPayload;   // tileId
    public final boolean boolPayload;
    public final Color colorPayload;// isUpper

    private GameMove(MoveType type, String nickname, int intPayload, char charPayload, boolean boolPayload, Color colorPayload) {
        this.type = type;
        this.nickname = nickname;
        this.intPayload = intPayload;
        this.charPayload = charPayload;
        this.boolPayload = boolPayload;
        this.colorPayload = colorPayload;
    }

    public static GameMove setNumPlayers(int numPlayers) {
        return new GameMove(MoveType.SET_NUM_PLAYERS,null,  numPlayers, '\0', false, null);
    }

    public static GameMove addPlayer(String nickname, Color color) {
        return new GameMove(MoveType.ADD_PLAYER, nickname,0, '\0', false, color);
    }

    public static GameMove startGame() {
        return new GameMove(MoveType.START_GAME, null, 0, '\0', false, null);
    }

    public static GameMove placeTotem(String nickname, char tileId) {
        return new GameMove(MoveType.PLACE_TOTEM, nickname, 0, tileId, false, null);
    }

    public static GameMove takeCard(String nickname, int cardIndex, boolean isUpper) {
        return new GameMove(MoveType.TAKE_CARD, nickname, cardIndex, '\0', isUpper, null);
    }

    public static GameMove skipExtraDraw(String nickname) {
        return new GameMove(MoveType.SKIP_EXTRA_DRAW, nickname, 0, '\0', false, null);
    }

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