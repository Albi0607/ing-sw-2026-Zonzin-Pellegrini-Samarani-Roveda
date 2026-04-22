package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.state.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=====================================================");
        System.out.println("                       🌋 MESOS ");
        System.out.println("=====================================================\n");

        // 1. INIZIALIZZAZIONE
        List<Player> players = List.of(
                new Player("Marco", Color.RED),
                new Player("Sofia", Color.BLUE)
        );
        Game game = new Game(players);
        game.startGame();

        // Variabile per evitare di stampare il round più volte nello stesso ciclo
        int lastPrintedRound = 0;

        while (game.getCurrentState().getStateId() != GameState.FINISHED) {

            GameStateLogic currentState = game.getCurrentState();

            // --- STAMPA DEL ROUND ---
            // Stampiamo il round solo quando cambia (tipicamente all'inizio del PlacingState)
            if (game.getCurrentRound() != lastPrintedRound) {
                System.out.println("\n#####################################################");
                System.out.println("📅 ROUND ATTUALE: " + game.getCurrentRound() + " | ERA: " + game.getCurrentEra());
                System.out.println("#####################################################");
                lastPrintedRound = game.getCurrentRound();
            }

            // --- GESTIONE STATO: PLACING (Piazzamento Totem) ---
            if (currentState instanceof PlacingState ps) {
                Player p = ps.getActivePlayer(game);

                // Stampa situazione giocatore
                stampaStatusGiocatore(p);

                System.out.println("--- 🚩 TURNO DI PIAZZAMENTO ---");
                List<OfferTile> available = game.getBoard().getAvailableTiles();
                System.out.println("Tessere disponibili:");
                for (OfferTile t : available) {
                    System.out.println(" -> [" + t.getId() + "] (Prendi: " + t.getUpperCount() + "▲, " + t.getLowerCount() + "▼ | Bonus: " + t.getFoodBonus() + "🍖)");
                }

                System.out.print("Scegli la lettera della tessera: ");
                char choice = scanner.next().toUpperCase().charAt(0);
                OfferTile selectedTile = game.getBoard().getTile(choice);

                try {
                    game.placeTotemOnOffer(p, selectedTile);
                } catch (Exception e) {
                    System.out.println("❌ Errore: " + e.getMessage() + " Riprova.");
                }
            }

            // --- GESTIONE STATO: RESOLVING (Risoluzione Azioni) ---
            else if (currentState instanceof ResolvingState rs) {
                Player p = rs.getActivePlayer(game);
                if (p == null) continue;

                // Stampa situazione giocatore ad ogni step di risoluzione
                stampaStatusGiocatore(p);

                System.out.println("--- ⚖️ FASE RISOLUZIONE ---");
                System.out.println("Azioni Rimanenti -> ▲ Superiori: " + rs.getRemainingUpper() + " | ▼ Inferiori: " + rs.getRemainingLower());
                stampaFilaCarte(game);

                try {
                    if (rs.getRemainingUpper() > 0) {
                        System.out.print("Scegli l'indice [ID] della carta SUPERIORE (▲): ");
                        int index = scanner.nextInt();
                        rs.takeCard(game, p, index, true);
                    } else if (rs.getRemainingLower() > 0) {
                        System.out.print("Scegli l'indice [ID] della carta INFERIORE (▼): ");
                        int index = scanner.nextInt();
                        rs.takeCard(game, p, index, false);
                    } else {
                        System.out.println("Esecuzione bonus tessera...");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Errore: " + e.getMessage());
                    scanner.nextLine();
                }
            }

            // --- GESTIONE STATI AUTOMATICI (Setup, Eventi) ---
            else if (currentState instanceof SetupState || currentState instanceof EventState) {
                currentState.execute(game);
            }
        }

        // 5. RESOCONTO FINALE
        System.out.println("\n=====================================================");
        System.out.println("🏆 PARTITA CONCLUSA - RISULTATI FINALI");
        System.out.println("=====================================================");
        for (Player p : game.getPlayers()) {
            stampaStatusGiocatore(p);
        }
    }

    /**
     * Stampa in modo compatto le risorse e la tribù del giocatore.
     */

    private static void stampaStatusGiocatore(Player p) {
        System.out.println("\n👤 SITUAZIONE GIOCATORE: " + p.getNickname().toUpperCase());
        System.out.println("   [ 🍖 Cibo: " + p.getFood() + " | ⭐️ Prestigio: " + p.getPrestigePoints() + " ]");
        System.out.println("   [ 👨‍👩‍👧‍👦 Personaggi: " + p.getTribe().getCharactersCount() + " | 🏛️ Edifici: " + p.getTribe().getBuildingsCount() + " ]");
        System.out.println("-----------------------------------------------------");
    }

    private static void stampaFilaCarte(Game game) {
        System.out.println("\n--- BOARD ATTUALE ---");
        List<Card> upper = game.getBoard().getUpperRow();
        List<Card> lower = game.getBoard().getLowerRow();

        System.out.print("▲ SOPRA: ");
        if (upper.isEmpty()) System.out.print("[Vuota]");
        for (int i = 0; i < upper.size(); i++) {
            Card c = upper.get(i);
            // Usiamo il tuo metodo getAsEventCard per vedere se è un evento
            EventCard ec = c.getAsEventCard();

            String tag;
            if (ec != null) {
                // Stampiamo il tipo specifico dell'evento (es. HUNT, SUSTENANCE)
                tag = "⚠️" + ec.getType();
            } else {
                tag = c.getClass().getSimpleName();
            }
            System.out.print("[" + i + ":" + tag + "] ");
        }

        System.out.print("\n▼ SOTTO: ");
        if (lower.isEmpty()) System.out.print("[Vuota]");
        for (int i = 0; i < lower.size(); i++) {
            Card c = lower.get(i);
            EventCard ec = c.getAsEventCard();

            String tag;
            if (ec != null) {
                tag = "⚠️" + ec.getType();
            } else {
                tag = c.getClass().getSimpleName();
            }
            System.out.print("[" + i + ":" + tag + "] ");
        }
        System.out.println("\n---------------------");
    }
}
