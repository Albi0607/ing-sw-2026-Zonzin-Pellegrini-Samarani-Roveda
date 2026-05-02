package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.view.CardRegistry;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.model.enums.Color;

import java.util.List;

public class CLIPrinter {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_GRAY = "\u001B[90m";

    /**
     * Converts a player's color enum into the corresponding ANSI escape code for CLI display.
     * @param color The color enum of the player.
     * @return The ANSI string for the specified color.
     */
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

    /**
     * Prints the game title, active players, current era, and round number.
     * @param gameDTO The current game data.
     * @param isWelcome Boolean flag to trigger a welcome message.
     */
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

    /**
     * Displays the complete game board including upper, central, and lower rows.
     * @param gameDTO The current game data containing the board state.
     */
    public static void printBoard(GameDTO gameDTO) {
        if (gameDTO.board == null) {
            System.out.println("Errore: BoardDTO non presente nel GameDTO.");
            return;
        }

        System.out.println("\n" + ANSI_BOLD + "[ FILA SUPERIORE ]" + ANSI_RESET);
        printCardRow(gameDTO.board.upperRow);

        System.out.println("\n" + ANSI_BOLD + "[ PLANCIA CENTRALE ]" + ANSI_RESET);
        printControlBoard(gameDTO.board);

        System.out.println("\n" + ANSI_BOLD + "[ FILA INFERIORE ]" + ANSI_RESET);
        printCardRow(gameDTO.board.lowerRow);
        System.out.println("----------------------------------------------------------\n");
    }

    /**
     * Formats and prints a horizontal row of cards using the appropriate strategy pattern formatters.
     * @param row The list of card DTOs to be displayed.
     */
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
                Formatters.CardFormatter<Object> formatter = FormattersRegistry.getFormatter(cardInfo.getClass());
                if (formatter != null) {
                    System.out.print(formatter.format(cardInfo) + "  ");
                }
            } else {
                System.out.print("[Carta Sconosciuta (ID: " + c.id + ")]  ");
            }
        }
        System.out.println();
    }

    /**
     * Displays detailed resource and tribe statistics for all players in the game.
     * @param gameDTO The current game data.
     */
    public static void printAllPlayersStatus(GameDTO gameDTO) {
        if (gameDTO.players == null) return;

        String foodIcon = VisualTheme.getSymbol("food");
        String prestigeIcon = VisualTheme.getSymbol("prestige");
        String colorReset = VisualTheme.getColor("RESET");

        System.out.println(ANSI_BOLD + "\n[ STATUS GIOCATORI ]" + colorReset);

        for (PlayerDTO p : gameDTO.players) {
            String color = getPlayerColorANSI(p.color);

            System.out.printf("%s%s%s -> Cibo: %s %d | Prestigio: %s %d\n",
                    color, p.nickname, colorReset, foodIcon, p.food, prestigeIcon, p.prestigePoints);

            int bCount = (p.tribe != null && p.tribe.buildings != null) ? p.tribe.buildings.size() : 0;
            int cCount = (p.tribe != null && p.tribe.characters != null) ? p.tribe.characters.size() : 0;
            System.out.printf("   Totali: %d Edifici | %d Personaggi\n", bCount, cCount);

            System.out.println("   Edifici: " + PlayerStatusLogic.getBuildingsString(p));
            System.out.println("   Tribù:   " + PlayerStatusLogic.getTribeString(p));
            System.out.println();
        }
    }

    /**
     * Prints the end-of-game screen, sorts players by prestige, and announces the winner.
     * @param gameDTO The final game data.
     */
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

    /**
     * Renders the central board section, showing the turn order and available offer tiles.
     * @param boardDTO The board data containing slots and tiles.
     */
    public static void printControlBoard(BoardDTO boardDTO) {
        StringBuilder tilesString = new StringBuilder();
        if (boardDTO.offerTiles != null) {
            for (OfferTileDTO tile : boardDTO.offerTiles) {
                String symbol = getTileSymbol(tile.id);
                String tileColor = (tile.occupantNickname != null) ? getPlayerColorANSI(tile.occupantColor) : ANSI_GRAY;

                tilesString.append(tileColor)
                        .append("[ ").append(tile.id).append(" : ").append(symbol).append(" ]")
                        .append(ANSI_RESET).append("   ");
            }
        }

        System.out.printf("%-38s | %s\n", "ORDINE DI TURNO", "TESSERE OFFERTA");
        System.out.println("---------------------------------------+--------------------------------------------------------");

        int numSlots = (boardDTO.turnOrderSlots != null) ? boardDTO.turnOrderSlots.size() : 0;

        if (numSlots == 0) {
            System.out.println("                                       | " + tilesString);
            return;
        }

        for (int i = 0; i < numSlots; i++) {
            TurnOrderSlotDTO slot = boardDTO.turnOrderSlots.get(i);

            String nick = (slot.occupantNickname != null) ? slot.occupantNickname : "  -  ";
            String color = (slot.occupantColor != null) ? getPlayerColorANSI(slot.occupantColor) : ANSI_GRAY;

            String modifierDisplay = getModifierSymbol(slot.modifier);

            String cleanModifier = modifierDisplay.replaceAll("\u001B\\[[;\\d]*m", "");
            String visibleText = (i + 1) + "° " + nick + " [ " + cleanModifier + " ]";

            int visualLength = visibleText.length();

            if (cleanModifier.contains("⭐")) {
                visualLength += 1;
            }

            int paddingNeeded = Math.max(0, 38 - visualLength);
            String padding = " ".repeat(paddingNeeded);

            String leftColumn = (i + 1) + "° " + color + nick + ANSI_RESET + " [ " + modifierDisplay + " ]" + padding;

            String rightColumn = (i == 0) ? tilesString.toString() : "";

            System.out.println(leftColumn + "| " + rightColumn);
        }
    }

    /**
     * Displays a dedicated section for resolved events and pauses execution for readability.
     * @param gameDTO The game data containing the last resolved events.
     */
    public static void printEventPhase(GameDTO gameDTO) {
        if (gameDTO.lastResolvedEvents == null || gameDTO.lastResolvedEvents.isEmpty()) {
            return;
        }

        System.out.println("\n" + ANSI_RED + ANSI_BOLD + "==========================================================" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "                 [ EVENT PHASE ]               " + ANSI_RESET);
        System.out.println(ANSI_RED + ANSI_BOLD + "==========================================================" + ANSI_RESET);

        for (String ev : gameDTO.lastResolvedEvents) {
            if (ev.equals("Nessun evento risolto.")) {
                System.out.println("   " + ANSI_GRAY + ev + ANSI_RESET);
            } else {
                System.out.println(" " + ANSI_YELLOW + "⚡ Risolto: " + ANSI_RESET + ev);
            }
        }
        System.out.println(ANSI_RED + ANSI_BOLD + "==========================================================\n" + ANSI_RESET);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns a symbolic string representation for a specific offer tile ID.
     * @param tileId The identifier of the tile.
     * @return A string containing icons and symbols for the tile.
     */
    private static String getTileSymbol(String tileId) {
        if (tileId == null) return "❓";
        return switch (tileId) {
            case "A" -> "+ 3🍗";
            case "B" -> " ↓ ";
            case "C" -> " ↑ ";
            case "D" -> "↓ ↓";
            case "E" -> "↑ ↓";
            case "F" -> "↑ ↑";
            case "G" -> "↑ ↑ ↓";
            default -> "❓";
        };
    }

    /**
     * Formats the turn order modifier into a color-coded string with resource icons.
     * @param modifier The numerical modifier of the turn order slot.
     * @return A formatted ANSI string representing the bonus or penalty.
     */
    private static String getModifierSymbol(int modifier) {
        if (modifier > 0) return ANSI_GREEN + "+" + modifier + " 🍗" + ANSI_RESET;
        if (modifier < 0) return ANSI_RED + modifier + " 🍗 / " + (modifier * 2) + " ⭐" + ANSI_RESET;
        return ANSI_GRAY + "  -  " + ANSI_RESET;
    }

}
