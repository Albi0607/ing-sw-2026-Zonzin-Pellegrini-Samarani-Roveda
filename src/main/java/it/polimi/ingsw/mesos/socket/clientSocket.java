package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.*;
import it.polimi.ingsw.mesos.socket.Message.messageClient.*;

import java.io.*;
import java.net.Socket;

public class clientSocket implements Network {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ClientController controller;

    public clientSocket(String host, int port) {
        try {
            socket = new Socket(host, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            new Thread(this::listenFromServer).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean register(String nickname, ClientController controller) {
        this.controller = controller;
        sendMessage(new RegisterMessage(nickname));
        return true;
    }

    @Override
    public boolean placeTotem(String nickname, char position) {
        sendMessage(new PlaceTotemMessage(nickname, position));
        return true;
    }

    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper) {
        sendMessage(new TakeCardMessage(nickname, position, isUpper));
        return true;
    }

    @Override
    public boolean choosePlayers(int numPlayers) {
        sendMessage(new ChoosePlayersMessage(numPlayers));
        return true;
    }

    @Override
    public boolean skipExtraDraw(String nickname) {
        sendMessage(new SkipExtraDrawMessage(nickname));
        return true;
    }

    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenFromServer() {
        try {
            while (true) {
                Message message = (Message) in.readObject();
                message.executeClientSide(controller);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}