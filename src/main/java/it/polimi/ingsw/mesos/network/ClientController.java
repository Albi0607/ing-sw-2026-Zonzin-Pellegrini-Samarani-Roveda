package it.polimi.ingsw.mesos.network;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.ClientModel.ClientState;
import it.polimi.ingsw.mesos.common.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.common.enums.Color;

import java.util.List;

/**
 * Core controller of the client-side application.
 *
 * This class manages all actions that a client can perform within the game and acts as a bridge
 * between the user interface (View) and the network layer (Network).
 * It is designed to be independent of the specific implementations of both the view (CLI/GUI)
 * and the network protocol (RMI/Socket), relying only on their common interfaces.
 * The controller also maintains the latest state of the game and the client, allowing it to
 * determine which actions are currently permitted.
 */
public class ClientController {
    /**
     * Network interface used to communicate with the server (RMI or Socket implementation).
     */
    private final Network network;
    /**
     * View interface used to display information to the user (CLI or GUI implementation).
     */
    private final View view;
    /**
     * Identifier of the server-side VirtualView associated with this client session.
     */
    private String virtualViewId;
    /**
     * Nickname of the current player.
     */
    private String nickname;
    /**
     * Current lobby state received from the server.
     */
    private List<LobbyInfoDTO> lobby;
    /**
     * Latest game state received from the server.
     */
    private GameDTO game;
    /**
     * Current state of the client (e.g. LOBBY, IN_GAME). Used to determine which actions are allowed.
     */
    private ClientState clientState;



    /**
     * Constructor of the class that requires as parameters the object responsible for managing the view type
     * (CLI or GUI) and the object responsible for managing the network type (RMI or socket)
     *
     * @param view object specific to the view type: CLI or GUI
     * @param network object specific to the network type: RMI or socket
     */
    public ClientController(View view, Network network){
        this.view = view;
        this.network = network;
    }

    /**
     * Updates the local game state and refreshes the view with the latest data.
     *
     * @param game the updated game state received from the server
     */
    public void updateGame(GameDTO game){
        this.game = game;
        view.showLastUpdate(game);
    }

    /**
     * Updates the client's state and notifies the view.
     * This state determines which actions the user is allowed to perform.
     *
     * @param state the updated client state
     */
    public void updateClientState(ClientState state){
        this.clientState = state;
        view.showClientStateUpdate(clientState);
    }

    /**
     * Updates and displays the current lobby state.
     *
     * @param lobby the list of available games in the lobby
     */
    public void showLobby(List<LobbyInfoDTO> lobby) {
        this.lobby=lobby;
        view.showLobby(lobby);
    }

    /**
     * Displays a general message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message){
        view.showMessage(message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param error the error message to display
     */
    public void showError(String error) {
        view.showMessage(error);
    }
    /**
     * Displays a message indicating that a requested action was rejected by the server.
     *
     * @param reason the reason for rejection
     */
    public void showActionRejected(String reason) {
        view.showActionRejected(reason);
    }
    /**
     * Displays a message confirming that an action was successfully accepted by the server.
     *
     * @param message confirmation message
     */
    public void showActionAccepted(String message) {
        view.showActionAccepted(message);
    }
    /**
     * Displays an error message related to login.
     *
     * @param message the login error message
     */
    public void showLoginError(String message){ view.showLoginError(message); }

    /**
     * Requests to place the player's totem on the OfferTile.
     * The action is only executed if it is the player's turn and the game state allows it.
     *
     * @param position the selected position on the OfferTile
     */
    public void placeTotem(char position){
        if(myTurnInGame(GameState.PLACING_TOTEMS)) {
            //Per gestire subito un eventuale errore di connessione
            if(!network.placeTotem(nickname, position)){
                view.showMessage("Errore piazzamento totem: Errore di connessione con il server");
            }
        }
        else{
            view.showMessage("Non puoi piazzare il totem poiché non tocca a te");
        }

    }

    /**
     * Requests to draw a card from the upper or lower row.
     * The action is only executed if it is the player's turn and the game state allows it.
     *
     * @param position the selected card position
     * @param isUpper true if the card is taken from the upper row, false otherwise
     */
    public void takeCard(int position,boolean isUpper){
        if(myTurnInGame(GameState.RESOLVING_ACTIONS)) {
            //Per gestire subito un eventuale errore di connessione
            if(!network.takeCard(nickname,position,isUpper)){
                view.showMessage("Errore nel scegliere la carta: Errore di connessione con il server");
            }
        }
        else{
            view.showMessage("Non puoi prendere la carta poiché non tocca a te");
        }

    }

    /**
     * Requests to skip the extra card draw at the end of the turn.
     * This action is only allowed if the player owns the required building that enables it.
     */
    public void skipOnExtraDraw(){
        if(myTurnInGame(GameState.RESOLVING_ACTIONS)){
            //Per gestire subito un eventuale errore di connessione
            if(!network.skipExtraDraw(nickname)){
                view.showMessage("Errore nel non pescare la carta: Errore di connessione con il server");
            }
        }
        else{
            view.showMessage("Non puoi scegliere di non pescare la carta poiché non tocca a te");
        }
    }

    /**
     * Checks whether it is currently the client's turn and whether the requested action is valid
     * for the current game state.
     *
     * @param state the required game state for the action
     * @return true if the client is allowed to perform the action; false otherwise
     */
    private boolean myTurnInGame(GameState state){
        return game != null && game.currentState == state && game.currentPlayerNickname.equals(nickname) &&
                clientState == ClientState.IN_GAME;
    }


    //metodi da eseguire nella lobby

    /**
     * Connects the client to the lobby and initializes the session with the server.
     *
     * @param nickname the player's nickname
     */
    public void getLobby(String nickname){
        this.virtualViewId=network.getLobby(nickname,this);
        this.nickname=nickname;
        this.clientState=ClientState.LOBBY;
    }

    /**
     * Sends a request to create a new game in the lobby.
     * The request is only valid when the client is currently in the lobby state.
     *
     * @param expectedNumPlayers the number of players required for the game
     * @param color the player's chosen color
     */
    public void createNewGame(int expectedNumPlayers, Color color){
        if(clientState!=ClientState.LOBBY){
            return;
        }
        if(virtualViewId==null){
            return;
        }
        //ti deve chiedere di inserire nickname e il numero di giocatori dalla view
        //Per gestire subito un eventuale errore di connessione
        if(!network.createNewGame(nickname,expectedNumPlayers, color,virtualViewId)){
            view.showMessage("Errore nel creare una nuova partita: Errore di connessione con il server");
        }
    }

    /**
     * Sends a request to join an existing game in the lobby.
     * The request is only valid when the client is currently in the lobby state.
     *
     * @param id the identifier of the game to join
     * @param color the player's chosen color
     */
    public void joinGame(int id, Color color){
        if(clientState!=ClientState.LOBBY){
            return;
        }
        if(virtualViewId==null){
            return;
        }
        if(!network.joinGame(nickname,id, color, virtualViewId)){
            //Per gestire subito un eventuale errore di connessione
            view.showMessage("Errore nel partecipare ad una partita: Errore di connessione con il server");
        }
    }

    /**
     * Displays the final leaderboard at the end of the game.
     *
     * @param leaderboard the list of ranked players
     * @param myPosition the position of the current player
     */
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition) {
        view.showLeaderboard(leaderboard, myPosition);
    }

}
