package it.polimi.ingsw.mesos.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    private static final String HOST_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/game_db_group26?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private static final String DB_NAME = "game_db_group26";

    private static Connection connection = null;
    private static boolean active = false;

    /**
     * Inizializza il database.
     * Se fallisce → il sistema continua senza DB.
     */
    public static void init(String user, String password) {

        try {
            // Connessione al server MySQL (senza DB)
            Connection rootConn = DriverManager.getConnection(HOST_URL, user, password);

            // Creazione database se non esiste
            try (Statement stmt = rootConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Connessione al database reale
            connection = DriverManager.getConnection(DB_URL, user, password);

            // Creazione tabella se non esiste
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS game_results (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nickname VARCHAR(50),
                        points INT,
                        num_players INT,
                        game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
            }

            active = true;
            System.out.println("✔ DB inizializzato correttamente");

        } catch (SQLException e) {
            active = false;
            connection = null;
            System.out.println("⚠ DB non disponibile: " + e.getMessage());
        }
    }

    /**
     * Ritorna la connessione attiva (può essere null se DB offline)
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Indica se il DB è utilizzabile
     */
    public static boolean isActive() {
        return active;
    }
}