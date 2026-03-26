package it.polimi.ingsw.mesos.model.board;

public class CreateTurnOrderTrack {

    public TurnOrderTrack initializeTurnOrderTrack(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        switch (numPlayers) {
            case 2:
                return new TurnOrderTrack(new int[]{1, -1});
            case 3:
                return new TurnOrderTrack(new int[]{2, 0, -1});
            case 4:
                return new TurnOrderTrack(new int[]{2, 1, 0, -1});
            case 5:
                return new TurnOrderTrack(new int[]{3, 1, 0, 0, -1});
            default:
                throw new IllegalStateException("Unexpected value: " + numPlayers);
        }
    }
}
