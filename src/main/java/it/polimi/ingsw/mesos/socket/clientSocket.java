package it.polimi.ingsw.mesos.socket;

import java.io.*;
import java.net.Socket;

public class clientSocket {

    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) {

        String hostName = "127.0.0.1";
        int portNumber = 7777;
        Socket echoSocket = null;

        try {
            echoSocket = new Socket(hostName, portNumber);
        } catch (IOException e) {
            System.err.println(e.toString() + " " + hostName);
            System.exit(1);
        }

        BufferedReader stdIn = null;

        try {
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            System.err.println(e.toString() + " " + hostName);
            System.exit(1);
        }

        String userInput;

        while (true) {
            try {
                if ((userInput = stdIn.readLine()) == null) break;

                if (userInput.startsWith("/nick")) {
                    chooseNickname(userInput.split(" ")[1]);

                } else if (userInput.startsWith("/players")) {
                    chooseNumPlayers(Integer.parseInt(userInput.split(" ")[1]));

                } else if (userInput.startsWith("/totem")) {
                    placeTotem(Integer.parseInt(userInput.split(" ")[1]));

                } else if (userInput.startsWith("/draw")) {
                    String[] parts = userInput.split(" ");
                    drawCard(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

                } else if (userInput.startsWith("/buy")) {
                    buyBuilding(Integer.parseInt(userInput.split(" ")[1]));

                } else {
                    sendMessage(userInput);
                }

                System.out.println("echo: " + in.readLine());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ===== METODO BASE =====
    public static void sendMessage(String msg) {
        out.println(msg);
    }

    // ===== AZIONI CLIENT =====

    public static void chooseNickname(String nickname) {
        sendMessage("NICKNAME " + nickname);
    }

    public static void chooseNumPlayers(int n) {
        sendMessage("NUM_PLAYERS " + n);
    }

    public static void placeTotem(int offerTileIndex) {
        sendMessage("PLACE_TOTEM " + offerTileIndex);
    }

    public static void drawCard(int row, int index) {
        sendMessage("DRAW_CARD " + row + " " + index);
    }

    public static void buyBuilding(int buildingId) {
        sendMessage("BUY_BUILDING " + buildingId);
    }
}