package it.polimi.ingsw.mesos.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * Manages the connection to the MySQL database used for storing game results.
 *
 * <p>This class provides static methods to initialize the database, retrieve the active
 * connection, and check whether the database is currently available. It is designed to
 * be fault-tolerant: if the database cannot be reached during initialization, the
 * application continues running without persistence rather than throwing a fatal error.
 *
 * <p>On initialization, the class will:
 * <ol>
 *   <li>Connect to the MySQL server (without selecting a database).</li>
 *   <li>Create the target database ({@value #DB_NAME}) if it does not already exist.</li>
 *   <li>Connect to the target database and create the {@code game_results} table if missing.</li>
 * </ol>
 */

public class DBManager {

    /** JDBC URL for connecting to the MySQL server without specifying a database. */
    private static final String HOST_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    /** JDBC URL for connecting directly to the {@value #DB_NAME} database. */
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/game_db_group26?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    /** Name of the database managed by this class. */
    private static final String DB_NAME = "game_db_group26";

    /**
     * The active database connection, or {@code null} if the database is unavailable.
     * Shared across all callers.
     */
    private static Connection connection = null;

    /**
     * Indicates whether the database was successfully initialized and is ready for use.
     * Set to {@code true} only after a successful call to {@link #init(String, String)}.
     */
    private static boolean active = false;

    /**
     * Initializes the database connection and ensures the required schema exists.
     *
     * <p>This method performs the following steps in order:
     * <ol>
     *   <li>Connects to the MySQL server using the provided credentials.</li>
     *   <li>Creates the {@value #DB_NAME} database if it does not already exist.</li>
     *   <li>Opens a connection to the target database.</li>
     *   <li>Creates the {@code game_results} table if it does not already exist,
     *       with columns: {@code id}, {@code nickname}, {@code points},
     *       {@code num_players}, and {@code game_date}.</li>
     * </ol>
     *
     * <p>If any step fails (e.g. the MySQL server is unreachable, or credentials are
     * invalid), the exception is caught and the database is marked as inactive.
     * The application can continue without database support by checking
     * {@link #isActive()} before attempting any queries.
     *
     * @param user     the MySQL username used for authentication
     * @param password the MySQL password used for authentication
     */
    public static void init(String user, String password) {

        try {
            // Connection to MySQL server (without DB)
            Connection rootConn = DriverManager.getConnection(HOST_URL, user, password);

            // Creation of the DB if it does not exist
            try (Statement stmt = rootConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            }

            // Connection to the real DB
            connection = DriverManager.getConnection(DB_URL, user, password);

            // Creation of the table if it does not exist
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
     * Returns the active database connection.
     *
     * <p>This method returns {@code null} if the database was not successfully
     * initialized or became unavailable. Callers should always check {@link #isActive()}
     * before using the returned connection.
     *
     * @return the active {@link Connection}, or {@code null} if the database is offline
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Indicates whether the database has been successfully initialized and is available.
     *
     * <p>Returns {@code true} only if {@link #init(String, String)} completed without
     * errors. Returns {@code false} if initialization was never called or if it failed
     * due to a {@link SQLException}.
     *
     * @return {@code true} if the database is active and usable, {@code false} otherwise
     */
    public static boolean isActive() {
        return active;
    }
}