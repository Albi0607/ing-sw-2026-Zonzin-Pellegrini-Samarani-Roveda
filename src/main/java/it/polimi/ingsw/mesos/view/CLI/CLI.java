package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.rete.View;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.Scanner;

public class CLI implements View {
    private final GameController controller;
    private final Scanner scanner;
    private String myNickname;

    private GameDTO currentGameState;
    private ClientState currentClientState;

    public CLI(GameController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        CLIPrinter.clearScreen();
        System.out.println(CLIPrinter.ANSI_YELLOW + "Benvenuto in Mesos!" + CLIPrinter.ANSI_RESET);

        setupGame();
    }

    @Override
    public void showLastUpdate(GameDTO game) {

        this.currentGameState = game;

        drawUI();

        handleTurn();
    }

    @Override
    public void showClientStateUpdate(ClientState state) {

        this.currentClientState = state;

    }

    @Override
    public void showMessage(String message) {
        // Questo è perfetto per gli errori ("Cibo insufficiente!")
        System.out.println(CLIPrinter.ANSI_RED + "🔔 NOTIFICA: " + message + CLIPrinter.ANSI_RESET);

        // Siccome c'è stato un errore, ricarichiamo la richiesta di input
        handleTurn();
    }
    private void setupGame() {
        System.out.print("Inserisci il tuo nickname: ");
        this.myNickname = scanner.nextLine().trim();

        VirtualView mockView = new VirtualView() {
            @Override public void sendGame(GameDTO gameDTO) { showLastUpdate(gameDTO); }
            @Override public void sendClientState(ClientState clientState) { showClientStateUpdate(clientState); }
            @Override public void showMessage(String message) { CLI.this.showMessage(message); }
            @Override public String getNickname() { return myNickname; }
        };

        controller.setNumPlayers(2);
        controller.addPlayer(myNickname, mockView);
        controller.addPlayer("Bot_AI", mockView);
        controller.startGame();
    }

    private void drawUI() {
        CLIPrinter.clearScreen();

        CLIPrinter.printHeader(currentGameState, false);
        CLIPrinter.printBoard(currentGameState);
        CLIPrinter.printAllPlayersStatus(currentGameState);

        System.out.println("Fase attuale: " + CLIPrinter.ANSI_GREEN + currentGameState.currentState + CLIPrinter.ANSI_RESET + "\n");
    }

    private void handleTurn() {

        if (currentGameState.currentState == GameState.FINISHED) {
            CLIPrinter.printGameOver(currentGameState);
            return;
        }

        if (currentGameState.currentPlayerNickname != null && currentGameState.currentPlayerNickname.equals(myNickname)) {

            if (currentGameState.currentState == GameState.PLACING_TOTEMS) {
                handlePlacement();
            } else if (currentGameState.currentState == GameState.RESOLVING_ACTIONS) {
                handleResolution();
            }

        } else {

            System.out.println("In attesa del turno di " + CLIPrinter.ANSI_CYAN + currentGameState.currentPlayerNickname + CLIPrinter.ANSI_RESET + "...");
        }
    }

    private void handlePlacement() {
        System.out.println("Tocca a: " + CLIPrinter.ANSI_CYAN + myNickname + CLIPrinter.ANSI_RESET);
        System.out.print("Scegli la tessera per il totem (A, B, C, D, E, F): ");

        String input = scanner.nextLine().trim().toUpperCase();

        if (input.isEmpty() || input.length() != 1) {
            System.out.println(CLIPrinter.ANSI_RED + "❌ Inserisci una singola lettera!" + CLIPrinter.ANSI_RESET);

            handlePlacement();
            return;
        }

        try {
            controller.onPlaceTotem(myNickname, input.charAt(0));

        } catch (Exception e) {
            showMessage("Mossa non valida: " + e.getMessage());
            handlePlacement();
        }
    }

    private void handleResolution() {

        System.out.println("Tocca a: " + CLIPrinter.ANSI_CYAN + myNickname + CLIPrinter.ANSI_RESET);


        boolean isUpper = true;
        System.out.println("Azione: Pesca dalla fila " + CLIPrinter.ANSI_YELLOW + "SUPERIORE (↑)" + CLIPrinter.ANSI_RESET);

        System.out.print("Digita il NUMERO della carta: ");
        String input = scanner.nextLine().trim();

        try {
            int cardIndex = Integer.parseInt(input) - 1;
            controller.onTakeCard(myNickname, cardIndex, isUpper);
        } catch (Exception e) {
            showMessage("Errore: " + e.getMessage());
            handleResolution();
        }
    }

    private void attendiInvio() {
        System.out.print(CLIPrinter.ANSI_YELLOW + "\nPremi INVIO per continuare..." + CLIPrinter.ANSI_RESET);
        scanner.nextLine();
    }
}