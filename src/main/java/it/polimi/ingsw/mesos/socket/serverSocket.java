package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class serverSocket {

    private final int portNumber;
    private final GameController controller;
    private final List<ClientHandler> clients = new ArrayList<>();

    //potrebbe servire non mi è ancora chiaro se il server deve in qualche modo usare questa lista per iterare/osservare i client (es. gestione disconnessioni)

    public serverSocket(int portNumber) {
        this.portNumber = portNumber;
        this.controller = new GameController();

    }
    public static void main(String[] args) {
        int portNumber = 1234;
        if (args.length > 0) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        new serverSocket(portNumber).start();
    }

    public void start() {
        System.out.println("Starting Socket server on port: " + portNumber + "...");

        // ricorda: notazione confusionaria, esiste serverSocket di tipo ServerSocket e serverSocket l'attuale classe, due cose diverse
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Listening. Waiting for players...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: "
                        + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, controller);
                clients.add(handler);

                Thread thread = new Thread(handler);
                thread.setDaemon(true); // il thread non blocca la JVM in chiusura, al contrario avrebbe dovuto aspettare che le connessioni finissero prima di potersi chiudere
                thread.setName("client-" + clientSocket.getInetAddress().getHostAddress());
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Fatal error: " + e.getMessage());
        }
    }

    //parte di chiusura, da caoire se serve con il loop
    /*System.out.println("Closing sockets.");
    try {
        ss.close();
        clientSocket.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    */
    /*
        // read the list of messages from the clientSocket
        List<Message> listOfMessages;
        try {
            listOfMessages = (List<Message>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received [" + listOfMessages.size() + "] messages from: " + clientSocket);
        // print out the text of every message
        System.out.println("All messages:");
        listOfMessages.forEach((msg)-> System.out.println(msg.toString()));
    }*/
}