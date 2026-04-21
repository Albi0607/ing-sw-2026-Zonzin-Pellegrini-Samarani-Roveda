package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.model.enums.GameState;

public class ClientController {

    private final Network network;
    private final View view;

    private String nickname;
    private GameDTO game;
    private ClientState clientState;

    public ClientController(View view, Network network){
        this.view = view;
        this.network = network;
        this.clientState=ClientState.WAITING_CONNECTION;
    }

    public void updateGame(GameDTO game){
        this.game = game;
        view.showLastUpdate(game);
    }

    //attenzione che qua game potrebbe es
    public void updateClientState(ClientState state){
        this.clientState = state;
        view.showClientStateUpdate(clientState);
    }

    //metto nell'attributo nickname del controller il nickname scelto oltre a effettuare la registrazione
    public void register(String nickname){
        if(clientState==ClientState.WAITING_CONNECTION) {
            if (network.register(nickname, this)) {
                this.nickname = nickname;
                view.showMessage("Registrazione avvenuta correttamente");
            }
        }
        else{
            view.showMessage("Errore nella registrazione");
        }

    }

    public void placeTotem(char position){
        if(game!=null&&game.currentState== GameState.PLACING_TOTEMS&&game.currentPlayerNickname.equals(nickname)&&clientState==ClientState.IN_GAME) {
            if(network.placeTotem(nickname, position)){
                view.showMessage("Totem piazzato correttamente");
            }
            else{
                view.showMessage("Errore piazzamento totem");
            }
        }
        else{
            view.showMessage("Non puoi piazzare il totem poiché non tocca a te");
        }

    }

    public void takeCard(int position,boolean isUpper){
        if(game!=null&&game.currentState== GameState.RESOLVING_ACTIONS&&game.currentPlayerNickname.equals(nickname)&&clientState==ClientState.IN_GAME) {
            if(network.takeCard(nickname,position,isUpper)){
                view.showMessage("Carta presa correttamente");
            }
            else{
                view.showMessage("Errore nel prendere la carta");
            }
        }
        else{
            view.showMessage("Non puoi prendere la carta poiché non tocca a te");
        }
    }

    public void choosePlayer(int numPlayers){
        if(clientState==ClientState.CHOOSE_PLAYERS) {
            if(network.choosePlayers(numPlayers)){
                view.showMessage("Numero di giocatori scelto correttamente");
            }
            else{
                view.showMessage("Numero di giocatori non scelto");
            }
        }
        else{
            view.showMessage("Non puoi scegliere il numero di giocatori perché non tocca a te");
        }
    }

    //aggiungere il metodo per fare una pescata extra


     public void showMessage(String message){
        view.showMessage(message);
     }

    public void showError(String error) {
        view.showMessage(error);
    }

}
