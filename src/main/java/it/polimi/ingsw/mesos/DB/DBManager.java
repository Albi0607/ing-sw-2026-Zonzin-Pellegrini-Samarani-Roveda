package it.polimi.ingsw.mesos.DB;

import java.sql.*;

public class DBManager {

    private static final String HOST_URL =
            "jdbc:mysql://localhost:3306/?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/game_db_group26?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

    private static final String DB_NAME = "game_db_group26";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    private static Connection connection;

    public static void init() throws SQLException {

        // 1) Connessione al server MySQL SENZA DB
        Connection rootConn = DriverManager.getConnection(HOST_URL, USER, PASSWORD);

        // 2) Creazione database se non esiste
        try (Statement stmt = rootConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        // 3) Connessione al database vero
        connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        // 4) Creazione tabella
        String createTable = """
            CREATE TABLE IF NOT EXISTS game_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nickname VARCHAR(50),
                points INT,
                num_players INT,
                game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTable);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}
