package it.polimi.ingsw.mesos.rete;

/**
 * Generic interface for network management; both the RMI client and the socket client must implement this interface so
 * that the ClientController can invoke network methods without knowing which network implementation is being used
 */
public interface Network {

    /**
     * Method that allows the client to register for a game session on the server side
     * @param nickname name chosen by the client
     * @param controller ClientController used to create a server-side reference, allowing the server to send game
     * updates and other types of messages to the client.
     * @return true if the registration was successful; otherwise, false
     */
    boolean register(String nickname, ClientController controller);

    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name of the player performing the action
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     */
    boolean placeTotem(String nickname,char position);

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name of the player performing the action
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     */
    boolean takeCard(String nickname,int position,boolean isUpper);

    /**
     * Method that allows the player (to be used only if they are the first connected player) to choose the number of
     * players participating in the game
     * @param numPlayers number of players selected, ranging from 2 to 5
     * @return true if the players were chosen successfully; otherwise, false
     */
    boolean choosePlayers(int numPlayers);

}
