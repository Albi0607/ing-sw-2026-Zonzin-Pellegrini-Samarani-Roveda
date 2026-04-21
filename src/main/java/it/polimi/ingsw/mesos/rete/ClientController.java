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
                System.out.println("Registrazione avvenuta correttamente");
            }else{
                System.out.println("ERRORE di registrazione ");//aggiunto questo else
            }
        }
        else{
            System.out.println("Errore nella registrazione");
        }

    }

    public void placeTotem(char position){
        if(game!=null&&game.currentState== GameState.PLACING_TOTEMS&&game.currentPlayerNickname.equals(nickname)&&clientState==ClientState.IN_GAME) {
            if(network.placeTotem(nickname, position)){
                System.out.println("Totem piazzato correttamente");
            }
            else{
                System.out.println("Errore piazzamento totem");
                view.showMessage("Tessera occupata o mossa non valida! Riprova."); // aggiunto per gestire l'errore di piazzamento tessera
            }
        }
        else{
            System.out.println("Non puoi piazzare il totem poiché non tocca a te");
        }

    }

    public void takeCard(int position,boolean isUpper){
        if(game!=null&&game.currentState== GameState.RESOLVING_ACTIONS&&game.currentPlayerNickname.equals(nickname)&&clientState==ClientState.IN_GAME) {
            if(network.takeCard(nickname,position,isUpper)){
                System.out.println("Carta presa correttamente");
            }
            else{
                System.out.println("Errore nel prendere la carta");
            }
        }
        else{
            System.out.println("Non puoi prendere la carta poiché non tocca a te");
        }
    }

    public void choosePlayer(int numPlayers){
        if(clientState==ClientState.CHOOSE_PLAYERS) {
            if(network.choosePlayers(numPlayers)){
                System.out.println("Numero di giocatori scelto correttamente");
            }
            else{
                System.out.println("Numero di giocatori non scelto");
            }
        }
        else{
            System.out.println("Non puoi scegliere il numero di giocatori perché non tocca a te");
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
