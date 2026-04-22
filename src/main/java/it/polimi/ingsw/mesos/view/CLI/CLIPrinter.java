package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.model.deck.BuildingCardJson;
import it.polimi.ingsw.mesos.model.deck.CardRegistry;
import it.polimi.ingsw.mesos.model.deck.CharacterCardJson;
import it.polimi.ingsw.mesos.model.deck.EventCardJson;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.Color;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CLIPrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static String getPlayerColorANSI(Color color) {
        if (color == null) return ANSI_WHITE;
        return switch (color) {
            case RED -> "\u001B[31m";
            case BLUE -> "\u001B[34m";
            case YELLOW -> "\u001B[33m";
            case PURPLE -> "\u001B[35m";
            case WHITE -> "\u001B[37m";
            default -> ANSI_WHITE;
        };
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printHeader(GameDTO gameDTO, boolean isWelcome) {
        System.out.println(ANSI_CYAN + ANSI_BOLD + "==========================================================" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "                      M E S O S                           " + ANSI_RESET);
        System.out.println(ANSI_CYAN + ANSI_BOLD + "==========================================================" + ANSI_RESET);

        if (isWelcome && gameDTO.players != null) {
            System.out.print("Benvenuti: ");
            for (PlayerDTO p : gameDTO.players) {
                System.out.print(getPlayerColorANSI(p.color) + p.nickname + ANSI_RESET + "  ");
            }
            System.out.println("\n");
        }

        System.out.println(ANSI_BOLD + "▶ ERA ATTUALE: " + gameDTO.era + "   |   ▶ ROUND: " + gameDTO.currentRound + ANSI_RESET);
        System.out.println("----------------------------------------------------------");
    }

    public static void printBoard(GameDTO gameDTO) {
        if (gameDTO.board == null) {
            System.out.println("Errore: BoardDTO non presente nel GameDTO.");
            return;
        }

        System.out.println("\n" + ANSI_BOLD + "[ FILA SUPERIORE ]" + ANSI_RESET);
        printCardRow(gameDTO.board.upperRow);


        //SISTEMARE uqesta parte per stampare tutta la plancia

        System.out.println("\n" + ANSI_BOLD + "[ PLANCIA CENTRALE ]" + ANSI_RESET);
        System.out.println(ANSI_RED + "⚠️ Impossibile stampare le Tessere Offerta e l'Ordine di Turno." + ANSI_RESET);
        System.out.println(ANSI_RED + "I dati mancano nel BoardDTO inviato dal Server!" + ANSI_RESET);

        System.out.println("\n" + ANSI_BOLD + "[ FILA INFERIORE ]" + ANSI_RESET);
        printCardRow(gameDTO.board.lowerRow);
        System.out.println("----------------------------------------------------------\n");
    }

    private static void printCardRow(List<CardDTO> row) {
        if (row == null || row.isEmpty()) {
            System.out.println("  (Fila vuota)");
            return;
        }

        for (int i = 0; i < row.size(); i++) {
            CardDTO c = row.get(i);
            System.out.print("(" + (i + 1) + ") ");


            Object cardInfo = CardRegistry.getCardInfo(c.id);

            if (cardInfo != null) {

                if (cardInfo instanceof CharacterCardJson charJson) {

                    String charName = (charJson.type != null) ? charJson.type.name() : "Sconosciuto";
                    System.out.print(ANSI_CYAN + charName + ANSI_RESET + "  ");

                } else if (cardInfo instanceof EventCardJson eventJson) {

                    String eventName = (eventJson.type != null) ? eventJson.type.name() : "Evento";
                    System.out.print(ANSI_RED + "⚡ " + eventName + ANSI_RESET + "  ");

                } else if (cardInfo instanceof BuildingCardJson buildJson) {

                    System.out.print(ANSI_YELLOW + "[Edificio - Costo: " + buildJson.cost + "]" + ANSI_RESET + "  ");

                }
            } else {
                System.out.print("[Carta Sconosciuta (ID: " + c.id + ")]  ");
            }
        }
        System.out.println();
    }

    public static void printAllPlayersStatus(GameDTO gameDTO) {
        if (gameDTO.players == null) return;

        System.out.println("[ STATUS GIOCATORI ]");
        for (PlayerDTO p : gameDTO.players) {
            String color = getPlayerColorANSI(p.color);

            int totalBuildings = (p.tribe != null && p.tribe.buildings != null) ? p.tribe.buildings.size() : 0;
            int totalCharacters = (p.tribe != null && p.tribe.characters != null) ? p.tribe.characters.size() : 0;

            String temp = "Nessuno";

            if (p.tribe != null && p.tribe.characters != null) {
                // Recuperiamo i tipi di personaggio dal Registry usando l'ID
                Map<String, Long> charGroups = p.tribe.characters.stream()
                        .map(c -> CardRegistry.getCardInfo(c.id))
                        .filter(info -> info instanceof CharacterCardJson)
                        .map(info -> (CharacterCardJson) info)
                        .filter(charJson -> charJson.type != null)
                        .collect(Collectors.groupingBy(charJson -> charJson.type.name(), Collectors.counting()));

                if (!charGroups.isEmpty()) {
                    temp = charGroups.entrySet().stream()
                            .map(e -> e.getKey() + ": " + e.getValue())
                            .collect(Collectors.joining(", "));
                }
            }

            System.out.printf("%s%s%s -> Cibo: 🍗 %d | Prestigio: ⭐ %d\n", color, p.nickname, ANSI_RESET, p.food, p.prestigePoints);
            System.out.printf("   Edifici totali: %d | Personaggi totali: %d (%s)\n\n",
                    totalBuildings, totalCharacters, temp);
        }
    }

    public static void printGameOver(GameDTO gameDTO) {
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "\n==========================================================");
        System.out.println("                     FINE PARTITA!                        ");
        System.out.println("==========================================================" + ANSI_RESET);

        if (gameDTO.players != null && !gameDTO.players.isEmpty()) {
            gameDTO.players.sort((p1, p2) -> Integer.compare(p2.prestigePoints, p1.prestigePoints));

            System.out.println("🏆 IL VINCITORE È " + getPlayerColorANSI(gameDTO.players.get(0).color) + gameDTO.players.get(0).nickname.toUpperCase() + ANSI_RESET + "!\n");

            for (int i = 0; i < gameDTO.players.size(); i++) {
                PlayerDTO p = gameDTO.players.get(i);
                System.out.printf("%d. %s%s%s - Prestigio Finale: ⭐ %d\n",
                        (i + 1), getPlayerColorANSI(p.color), p.nickname, ANSI_RESET, p.prestigePoints);
            }
        }
    }
}
