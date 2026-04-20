package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.socket.Message.messageClient.RegisterMessage;
import it.polimi.ingsw.mesos.socket.Message.messageServer.ErrorMessage;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final GameController controller;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String nickname;

    private SocketVirtualView virtualView;


    public ClientHandler(Socket clientSocket, GameController controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            setupStreams();
            clientLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** inizializza gli stream */
    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     *  Legge messaggi dal client finché la connessione è aperta.
     *  Il primo messaggio deve essere sempre un RegisterMessage.
     */
    private void clientLoop() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnesso: " + nickname);
        }
    }

    /**
     * gestisce i messaggi in arrivo, l'obiettivo è quello di distinguere i messaggi normali
     * da quelli di registrazione e di errore anche quando non c'è ancora nessuna virtualView
     */
    private void handleMessage(Message message) throws IOException {
        if (message instanceof RegisterMessage reg) {
            handleRegister(reg);
            return;
        }

        if (nickname == null) {
            // Client non ancora registrato → ignora qualsiasi altro messaggio
            System.err.println("Messaggio ricevuto prima della registrazione, ignorato.");
            return;
        }

        try {
            message.executeServerSide(controller);
        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
            // Errore di logica di gioco → risponde solo a questo client, non broadcast
            if (virtualView != null) {
                virtualView.showMessage(e.getMessage());
            }
        }
    }

    /**
     * Registra il client: crea la SocketVirtualView e la passa al controller.
     * In caso di errore (es. nickname già usato) risponde con un errore e chiude la connessione.
     */
    private void handleRegister(RegisterMessage reg) throws IOException {
        try {
            this.nickname    = reg.getNickname();
            this.virtualView = new SocketVirtualView(nickname, out);
            controller.addPlayer(nickname, virtualView);
            System.out.println( "Registrato: " + nickname);
        } catch (Exception e) {
            System.err.println("Errore registrazione '"
                    + reg.getNickname() + "': " + e.getMessage());
            // Manda errore e chiude — il client dovrà riconnettersi
            try {
                out.writeObject(new ErrorMessage(e.getMessage()));
                out.flush();
            } catch (IOException ignored) {}
            clientSocket.close(); // motivo del throws nella firma, potrebbe generare un'ecc inaspettata
        }
    }


}
