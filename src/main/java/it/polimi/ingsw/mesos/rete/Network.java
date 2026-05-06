package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.util.List;

/**
 * Generic interface for network management; both the RMI client and the socket client must implement this interface so
 * that the ClientController can invoke network methods without knowing which network implementation is being used
 */
public interface Network {


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
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     * @param nickname name of the player performing the action
     * @return true if the client has chosen not to draw the extra card due to the effect of the triggering building;
     * otherwise, false
     */
    boolean skipExtraDraw(String nickname);


    String getLobby(String nickname,ClientController controller);

    //azioni del client sulla lobby
    boolean createNewGame(String nickname, int expectedNumPlayers, String viewId);

    boolean joinGame(String nickname, int id, String viewId);

}
