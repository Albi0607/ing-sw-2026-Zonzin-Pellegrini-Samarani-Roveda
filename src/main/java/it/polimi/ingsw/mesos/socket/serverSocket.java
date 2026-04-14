package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.model.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static it.polimi.ingsw.mesos.socket.ClientHandler.GSON;


public class serverSocket {

    static int portNumber = 7777;
    static ArrayList<ClientHandler> clients = new ArrayList<>();
    //potrebbe servire non mi è ancora chiaro se il server deve in qualche modo usare questa lista per iterare/osservare i client (es. gestione disconnessioni)

    public static void main(String[] args) {
        System.out.println("Server started!");
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(portNumber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Listening on port " + portNumber);
        //loop infinito in accettazione di connessioni
        while (true) {

            Socket clientSocket = null;

            try {
                clientSocket = ss.accept(); //bloccante! se fate partire il programma, rimane inchiodato qua finchè non gli arriva una richiesta
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostAddress());
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            Thread t = new Thread(clientHandler);
            t.start();
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