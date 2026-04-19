package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message_prova;

import java.io.*;
import java.net.Socket;

public class clientHandler_prova implements Runnable {

    private final Socket clientSocket;
    private final GameController controller;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String nickname;

    public clientHandler_prova(Socket clientSocket, GameController controller) {
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

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void clientLoop() {
        try {
            while (true) {
                Message_prova message = (Message_prova) in.readObject();

                message.executeServerSide(controller);

            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnesso: " + nickname);
        }
    }

    // metodo per inviare messaggi al client
    public void send(Message_prova message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
