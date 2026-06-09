package it.polimi.ingsw.mesos.persistence;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes moves to disk and reads them back for game restoration.
 *
 * File format: a sequence of serialized Java objects (ObjectOutputStream),
 * one per move, appended to the end of the file after each action.
 *
 * Design choice: Java serialization is used (the same mechanism as socket messages)
 * instead of JSON for consistency with the rest of the project.
 *
 * Thread-safety: append() is synchronized because it can be called
 * from different ClientHandler threads (one for each connected client).
 */
public class MoveLogger {

    private final Path logFile;

    /**
     * Constructs a MoveLogger.
     *
     * @param logFilePath path to the log file, e.g., "mesos_game.log"
     */
    public MoveLogger(String logFilePath) {
        this.logFile = Paths.get(logFilePath);
    }

    /**
     * Appends a move to the end of the log file.
     * Creates the file if it does not exist.
     *
     * ObjectOutputStream has a limitation: each instance writes a stream header.
     * If a new ObjectOutputStream were opened for every append, the file would
     * contain multiple headers, making it unreadable by ObjectInputStream.
     * AppendingObjectOutputStream is used to circumvent this.
     *
     * @param move the game move to log
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
            System.err.println("[MoveLogger] Error writing move: " + e.getMessage());
        }
    }

    /**
     * Reads all moves from the log file in the order they were written.
     *
     * @return a sorted list of moves, empty if the file does not exist
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
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[MoveLogger] Error reading log: " + e.getMessage());
        }

        return moves;
    }

    /**
     * Deletes the log file and all other persistence files associated with
     * this game session: the tribe deck file, the building deck
     * file, and the player order file.
     *
     * <p>Called when a stale session is detected at startup so that orphaned
     * files do not accumulate on disk.
     *
     * @param gameId the game ID whose files should all be removed
     */
    public static void deleteAll(int gameId) {
        String[] paths = {
                "mesos_game_"    + gameId + ".log",
                "mesos_tribe_"   + gameId + ".txt",
                "mesos_building_" + gameId + ".txt",
                "mesos_order_"   + gameId + ".txt"
        };
        for (String p : paths) {
            try {
                boolean deleted = Files.deleteIfExists(Paths.get(p));
                if (deleted) {
                    System.out.println("Deleted stale file: " + p);
                }
            } catch (IOException e) {
                System.err.println("Could not delete " + p
                        + ": " + e.getMessage());
            }
        }
    }

    /**
     * Returns {@code true} if the log file exists and its last-modified timestamp
     * is older than {@code thresholdMinutes} minutes.
     *
     * <p>Used at server startup to detect sessions that crashed and were never
     * resumed: if the log is stale the session is considered abandoned and its
     * files can be safely deleted without attempting a restore.
     *
     * @param thresholdMinutes minutes of inactivity after which the session is
     *                         considered abandoned
     * @return {@code true} if the log is stale, {@code false} otherwise
     */
    public boolean isStale(long thresholdMinutes) {
        if (!Files.exists(logFile)) return false;
        try {
            FileTime lastModified = Files.getLastModifiedTime(logFile);
            Duration age = Duration.between(lastModified.toInstant(), Instant.now());
            return age.toMinutes() >= thresholdMinutes;
        } catch (IOException e) {
            System.err.println("[MoveLogger] Could not read log timestamp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns true if a log exists from which a game can be restored.
     *
     * @return true if a non-empty log file exists
     */
    public boolean hasSavedGame() {
        return Files.exists(logFile) && logFile.toFile().length() > 0;
    }

    /**
     * An ObjectOutputStream that overrides writeStreamHeader() as a no-op,
     * allowing objects to be appended to an existing file without duplicating
     * the stream header, the file already has one from the initial opening.
     */
    private static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }
}