package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.common.enums.Color;

import java.util.List;

/**
 * Class that manages all actions a client can perform within the game. It maintains a generic reference to both the
 * view being displayed and the network type used to communicate with the server, so that it does not depend on specific
 * implementations but instead invokes the common methods provided by the two generic interfaces
 *
 * It also holds a reference to the game, in particular to its latest updated state (if everything is functioning
 * correctly), and, through interaction with the server, it uses the clientState attribute to determine which actions
 * the client is required to perform
 */
public class ClientController {

    private final Network network;
    private final View view;
    private String virtualViewId;

    private String nickname;
    private List<LobbyInfoDTO> lobby;
    private GameDTO game;
    private ClientState clientState;



    /**
     * Constructor of the class that requires as parameters the object responsible for managing the view type
     * (CLI or GUI) and the object responsible for managing the network type (RMI or socket)
     * @param view object specific to the view type: CLI or GUI
     * @param network object specific to the network type: RMI or socket
     */
    public ClientController(View view, Network network){
        this.view = view;
        this.network = network;
    }

    /**
     * Method that allows the client to display the game updated to reflect the latest changes from the model
     * @param game latest game update
     */
    public void updateGame(GameDTO game){
        this.game = game;
        view.showLastUpdate(game);
    }

    /**
     * Method that updates the attribute indicating the user's current state and, consequently, the actions they must
     * perform (mostly before the start of the game)
     * @param state latest updated client state
     */
    public void updateClientState(ClientState state){
        this.clientState = state;
        view.showClientStateUpdate(clientState);
    }

    //metodo per mostrare la lobby
    public void showLobby(List<LobbyInfoDTO> lobby) {
        this.lobby=lobby;
        view.showLobby(lobby);
    }

    /**
     * Method that allows the client to display general messages related to the connection and game progress
     * @param message message to be displayed in the client view
     */
    public void showMessage(String message){
        view.showMessage(message);
    }

    /**
     * Method that allows the client to display error messages related to the connection and game progress
     * @param error error to be displayed in the client view
     */
    public void showError(String error) {
        view.showMessage(error);
    }

    public void showActionRejected(String reason) {
        view.showActionRejected(reason);
    }

    public void showActionAccepted(String message) {
        view.showActionAccepted(message);
    }

    public void showLoginError(String message){ view.showLoginError(message); }

    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param position position selected on the OfferTile
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
     * Method that allows the player to draw a card from the upper or lower row
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
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
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     */
    //definire meglio questa azione insieme a takeCard poiché avvengono entrambe nello stesso stato e quindi potrebbe
    //non esserci differenziazione da parte del client
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
     * Private method used within this class by other methods to check whether it is the client’s turn, and therefore
     * determine whether the client is allowed to perform certain actions or not
     * @param state state in which the game should be in order to perform a specific action
     * @return true if the state is correct and the client is therefore allowed to perform the action; otherwise, false.
     */
    private boolean myTurnInGame(GameState state){
        return game != null && game.currentState == state && game.currentPlayerNickname.equals(nickname) && clientState == ClientState.IN_GAME;
    }


    //metodi da eseguire nella lobby

    //metodo iniziale per accedere alla lobby poi la lobby si aggiornerà automaticamente
    public void getLobby(String nickname){
        this.virtualViewId=network.getLobby(nickname,this);
        this.nickname=nickname;
        this.clientState=ClientState.LOBBY;
    }

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

}
