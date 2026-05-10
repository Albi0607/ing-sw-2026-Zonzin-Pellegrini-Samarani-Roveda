package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.deck.Deck;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Salva e ripristina l'ordine dei deck su disco.
 *
 * Non tocca il model: usa solo i metodi pubblici draw() e put() di Deck.
 *
 * Formato file: serializzazione Java di una List<T> ordinata top→bottom.
 * Il file viene creato in startGame() e caricato dal GameRestorer prima
 * di rigiocare le mosse — in questo modo il mazzo al replay è identico
 * all'originale.
 *
 * Due file per partita:
 *   mesos_tribe_{gameId}.deck    → ordine mazzo tribù
 *   mesos_building_{gameId}.deck → ordine mazzo edifici
 */
public class DeckSerializer {

    private final String tribeFile;
    private final String buildingFile;

    public DeckSerializer(int gameId) {
        this.tribeFile    = "mesos_tribe_"    + gameId + ".deck";
        this.buildingFile = "mesos_building_" + gameId + ".deck";
    }

    /**
     * Svuota il deck con draw(), salva la lista ordinata su disco,
     * poi rimette tutte le carte in coda.
     *
     * Dopo questa chiamata il deck è identico a prima.
     * L'ordine salvato è top→bottom (prima carta = prima che verrebbe pescata).
     */
    public <T extends Card> void saveDeck(Deck<T> deck, boolean isTribe) {
        List<T> cards = drainDeck(deck);
        String path = isTribe ? tribeFile : buildingFile;
        writeDeckToFile(cards, path);
        // Rimette le carte in ordine inverso (put() aggiunge in cima)
        for (int i = cards.size() - 1; i >= 0; i--) {
            deck.put(cards.get(i));
        }
    }

    /**
     * Carica l'ordine salvato e lo applica al deck attuale.
     *
     * Prima svuota il deck (scartando le carte generate casualmente da startGame()),
     * poi ricostruisce il deck nell'ordine originale usando put() al contrario.
     *
     * @return true se il file esiste ed il ripristino è riuscito, false altrimenti
     */
    public <T extends Card> boolean restoreDeck(Deck<T> currentDeck,
                                                Deck<T> sourceDeck,
                                                boolean isTribe) {
        String path = isTribe ? tribeFile : buildingFile;

        if (!Files.exists(Paths.get(path))) {
            System.err.println("[DeckSerializer] File non trovato: " + path);
            return false;
        }

        // Costruisce una mappa id → carta dal deck attuale (generato casualmente)
        List<T> current = drainDeck(currentDeck);
        java.util.Map<String, T> byId = new java.util.HashMap<>();
        for (T card : current) {
            byId.put(card.getId(), card);
        }

        // Legge l'ordine originale dal file
        List<String> savedIds = readDeckFromFile(path);
        if (savedIds == null || savedIds.isEmpty()) return false;

        // Ricostruisce il deck nell'ordine originale
        // put() aggiunge in cima → inseriamo in ordine inverso (bottom first)
        for (int i = savedIds.size() - 1; i >= 0; i--) {
            T card = byId.get(savedIds.get(i));
            if (card != null) {
                currentDeck.put(card);
            } else {
                System.err.println("[DeckSerializer] Carta non trovata per id: " + savedIds.get(i));
            }
        }

        System.out.println("[DeckSerializer] Deck ripristinato: "
                + (isTribe ? "tribe" : "building") + " (" + savedIds.size() + " carte)");
        return true;
    }

    /**
     * Elimina i file deck di questa partita.
     * Chiamato da GameController.endGame() insieme a moveLogger.delete().
     */
    public void delete() {
        try {
            Files.deleteIfExists(Paths.get(tribeFile));
            Files.deleteIfExists(Paths.get(buildingFile));
            System.out.println("[DeckSerializer] File deck eliminati.");
        } catch (IOException e) {
            System.err.println("[DeckSerializer] Errore eliminazione deck: " + e.getMessage());
        }
    }

    public boolean hasSavedDecks() {
        return Files.exists(Paths.get(tribeFile))
                && Files.exists(Paths.get(buildingFile));
    }

    /**
     * Svuota il deck con draw() e restituisce le carte in ordine top→bottom.
     * Il deck rimane VUOTO dopo questa chiamata — il chiamante è responsabile
     * di rimetterle con put().
     */
    private <T extends Card> List<T> drainDeck(Deck<T> deck) {
        List<T> cards = new ArrayList<>();
        while (!deck.isEmpty()) {
            cards.add(deck.draw());
        }
        return cards;
    }

    /**
     * Salva la lista di ID su file.
     * L'ordine del mazzo è invertito.
     */
    @SuppressWarnings("unchecked")
    private <T extends Card> void writeDeckToFile(List<T> cards, String path) {
        List<String> ids = new ArrayList<>();
        for (T card : cards) {
            ids.add(card.getId());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(ids);
            oos.flush();
        } catch (IOException e) {
            System.err.println("[DeckSerializer] Errore scrittura " + path + ": " + e.getMessage());
        }
    }

    /**
     * Legge la lista di ID da file.
     * L'ordine del mazzo viene re-invertito in quello originale.
     */
    @SuppressWarnings("unchecked")
    private List<String> readDeckFromFile(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path))) {
            return (List<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[DeckSerializer] Errore lettura " + path + ": " + e.getMessage());
            return null;
        }
    }
}