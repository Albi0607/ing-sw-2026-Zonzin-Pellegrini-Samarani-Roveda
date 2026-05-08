package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;
import it.polimi.ingsw.mesos.socket.Message.messageServer.ClientStateMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.ErrorMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.LobbyUpdateMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.UpdateGameMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Implementazione di VirtualView per il protocollo Socket.
 *
 * Traduce i metodi del controller in messaggi
 * serializzati sull'ObjectOutputStream.
 *
 * Creata dal ClientHandler dopo aver ricevuto RegisterMessage,
 * quando il nickname è noto e gli stream sono pronti.
 */
public class SocketVirtualView implements VirtualView {

    private String nickname;
    private final ObjectOutputStream out;

    private final String id = java.util.UUID.randomUUID().toString();

    public SocketVirtualView(String nickname, ObjectOutputStream out) {
        this.nickname = nickname;
        this.out      = out;
    }

    /**
     * Invia lo stato aggiornato del gioco al client.
     * Chiamato da GameController.broadcastUpdate() dopo ogni azione.
     */
    @Override
    public synchronized void sendGame(GameDTO game) {
        send(new UpdateGameMessage(game));
    }

    /**
     * Notifica il client di un cambio di stato.
     */
    @Override
    public synchronized void sendClientState(ClientState state) {
        send(new ClientStateMessage(state));
    }

    /**
     * Invia un messaggio testuale al client
     * Non viene fatto broadcast — va solo a questo client.
     */
    @Override
    public synchronized void showMessage(String message) {
        send(new ErrorMessage(message));
    }

    /**
     * Scrive l'oggetto sullo stream out.
     */
    private synchronized void send(Object message) {
        try {
            out.writeObject(message);
            out.flush();
            // reset() evita che ObjectOutputStream tenga in cache
            // oggetti già inviati: senza reset, invii successivi dello stesso
            // oggetto (anche se modificato) potrebbero mandare la cache invece
            // del nuovo stato.
            out.reset();
        } catch (IOException e) {
            System.err.println("Errore invio a " + nickname + ": " + e.getMessage());
        }
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;

    }

    @Override
    public synchronized void sendLobby(List<LobbyInfoDTO> lobby) {
        send(new LobbyUpdateMessage(lobby));
    }

    @Override
    public String getId() {
        return id;
    }
}