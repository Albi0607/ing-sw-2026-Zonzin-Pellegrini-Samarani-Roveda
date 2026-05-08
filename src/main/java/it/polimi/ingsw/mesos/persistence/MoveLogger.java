package it.polimi.ingsw.mesos.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Scrive le mosse su disco e le rilegge per il ripristino.
 *
 * Formato del file: sequenza di oggetti Java serializzati (ObjectOutputStream),
 * uno per mossa, appesi in coda ad ogni azione.
 *
 * Scelta: usiamo serializzazione Java (stesso meccanismo dei messaggi socket)
 * invece di JSON per coerenza con il resto del progetto.
 *
 * Thread-safety: append() è synchronized perché può essere chiamato
 * da thread di ClientHandler diversi (uno per ogni client connesso).
 */
public class MoveLogger {

    private final Path logFile;

    /**
     * @param logFilePath percorso del file di log, es. "mesos_game.log"
     */
    public MoveLogger(String logFilePath) {
        this.logFile = Paths.get(logFilePath);
    }

    // ── Scrittura ────────────────────────────────────────────────────────────

    /**
     * Aggiunge una mossa in coda al file di log.
     * Crea il file se non esiste.
     *
     * ObjectOutputStream ha un problema: ogni istanza scrive un header
     * di stream. Se aprissimo un nuovo ObjectOutputStream ad ogni append,
     * il file avrebbe header multipli e ObjectInputStream non riuscirebbe
     * a leggerlo. Usiamo AppendingObjectOutputStream per evitarlo.
     */
    public synchronized void append(GameMove move) {
        try {
            boolean fileExists = Files.exists(logFile);

            try (FileOutputStream fos = new FileOutputStream(logFile.toFile(), true);
                 ObjectOutputStream oos = fileExists
                         ? new AppendingObjectOutputStream(fos)
                         : new ObjectOutputStream(fos)) {

                oos.writeObject(move);
                oos.flush();
            }

        } catch (IOException e) {
            System.err.println("[MoveLogger] Errore scrittura mossa: " + e.getMessage());
        }
    }

    /**
     * Legge tutte le mosse dal file di log nell'ordine in cui sono state scritte.
     *
     * @return lista ordinata delle mosse, vuota se il file non esiste
     */
    public List<GameMove> readAll() {
        List<GameMove> moves = new ArrayList<>();

        if (!Files.exists(logFile)) {
            return moves;
        }

        try (FileInputStream fis = new FileInputStream(logFile.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            while (true) {
                try {
                    GameMove move = (GameMove) ois.readObject();
                    moves.add(move);
                } catch (EOFException e) {
                    break; // fine file, lettura completata
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[MoveLogger] Errore lettura log: " + e.getMessage());
        }

        return moves;
    }

    /**
     * Elimina il file di log.
     * Chiamato quando la partita termina normalmente — non serve più il log.
     */
    public void delete() {
        try {
            Files.deleteIfExists(logFile);
            System.out.println("[MoveLogger] Log eliminato.");
        } catch (IOException e) {
            System.err.println("[MoveLogger] Errore eliminazione log: " + e.getMessage());
        }
    }

    /**
     * Restituisce true se esiste un log da cui ripristinare una partita.
     */
    public boolean hasSavedGame() {
        return Files.exists(logFile) && logFile.toFile().length() > 0;
    }

    /**
     * ObjectOutputStream che sovrascrive writeStreamHeader() con un no-op,
     * in modo da poter appendere oggetti a un file già esistente senza
     * duplicare l'header dello stream.
     */
    private static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // Non scrivere l'header: il file ne ha già uno dall'apertura iniziale.
            reset();
        }
    }
}