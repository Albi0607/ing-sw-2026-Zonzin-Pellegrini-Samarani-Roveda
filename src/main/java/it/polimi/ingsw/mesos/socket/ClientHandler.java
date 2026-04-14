package it.polimi.ingsw.mesos.socket;

import com.google.gson.Gson;
import it.polimi.ingsw.mesos.controller.GameController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//serve a gestire socket multipli connessi da diverse macchine al server
//nel costruttore infatti passi la singola macchina connessa
public class ClientHandler implements Runnable {

    static final Gson GSON = new Gson();

    private Socket clientSocket;
    //private GameController controller;
    private PrintWriter out;

    private String nickname;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
       // this.controller = controller;
    }

    @Override
    public void run() {
        try{
            clientLoop(clientSocket/*, controller*/);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // serve a leggere tutti i messaggi da un client girandoli al controller
    public void clientLoop(Socket clientSocket/*, GameController controller*/) throws  IOException {

        BufferedReader in = null;

        try {
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
// waits for data and reads it in until connection dies
// readLine() blocks until the server receives
// a new line from client
        String line;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                out.println(line.toUpperCase());
                //Message msg = GSON.fromJson(line, Message.class);
                //dispatch(msg, controller);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client: " + nickname + " disconnected");
    }
/*
    private void dispatch(Message msg, GameController gc) {
        if (msg == null || msg.action == null) {
            sendError("Message not valid");
            return;
        }

        try {
            switch (msg.action) {

                case "ADD_PLAYER":
                    this.nickname = msg.nickname;
                    gc.addPlayer(msg.nickname);
                    sendAck("Player added: " + msg.nickname);
                    break;

                case "START_GAME":
                    gc.startGame();
                    // onModelChanged() farà il broadcast automaticamente
                    break;

                case "PLACE_TOTEM":
                    gc.onPlaceTotem(msg.nickname, msg.tileId.charAt(0));
                    break;

                case "TAKE_UPPER":
                    gc.onTakeCardFromUpper(msg.nickname, msg.cardIndex);
                    break;

                case "TAKE_LOWER":
                    gc.onTakeCardFromLower(msg.nickname, msg.cardIndex);
                    break;

                default:
                    sendError("Azione sconosciuta: " + msg.action);
            }

        } catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException e) {
            // Errori di logica di gioco → torna solo al client che ha sbagliato
            sendError(e.getMessage());
        }
    }

    public void send(Message message) {
        if (out != null) {
            out.println(GSON.toJson(message));
        }
    }
    private void sendError(String text) {
        send(Message.withPayload("ERROR", text));
    }

    private void sendAck(String text) {
        send(Message.withPayload("ACK", text));
    }

    private void close() {
        try {
            clientSocket.close();
        } catch (IOException ignore) {}
    }*/

}