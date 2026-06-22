package it.polimi.ingsw.mesos.network;

import it.polimi.ingsw.mesos.common.enums.Color;

/**
 * Generic interface for network management; both the RMI client and the socket client must implement this interface so
 * that the ClientController can invoke network methods without knowing which network implementation is being used
 */
public interface Network {


    /**
     * Method that allows the client to place the totem on the OfferTile
     *
     * @param nickname name of the player performing the action
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     */
    boolean placeTotem(String nickname,char position);

    /**
     * Method that allows the player to draw a card from the upper or lower row
     *
     * @param nickname name of the player performing the action
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     */
    boolean takeCard(String nickname,int position,boolean isUpper);

    /**
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     *
     * @param nickname name of the player performing the action
     * @return true if the client has chosen not to draw the extra card due to the effect of the triggering building;
     * otherwise, false
     */
    boolean skipExtraDraw(String nickname);

    /**
     * This method retrieves the list of available game sessions and updates the
     * local lobby view. It also returns the identifier of the VirtualView
     * associated with the current ClientController, which is later used by the
     * server to move the client from the lobby context to a game context when a
     * game is created or joined.
     *
     * @param nickname the nickname of the requesting player
     * @param controller the client controller handling the received data
     * @return the VirtualView identifier associated with the client
     */
    String getLobby(String nickname,ClientController controller);

    /**
     * Creates a new game session in the server lobby.
     * The player becomes the host of the new game and defines both the expected
     * number of participants and the color of their own totem, which is assigned
     * at the start of the game.
     *
     * @param nickname the name of the player creating the game
     * @param expectedNumPlayers the number of players required for the game
     * @param color the color chosen by the player
     * @param viewId the identifier of the client view connection
     * @return true if the game was successfully created; otherwise false
     */
    boolean createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId);

    /**
     * Joins an existing game session in the lobby.
     * The player is added to the specified game if it exists and has available slots.
     *
     * @param nickname the name of the player joining the game
     * @param id the identifier of the game to join
     * @param color the color selected by the player
     * @param viewId the identifier of the client view connection
     * @return true if the player successfully joined the game; otherwise false
     */
    boolean joinGame(String nickname, int id, Color color,String viewId);

}
