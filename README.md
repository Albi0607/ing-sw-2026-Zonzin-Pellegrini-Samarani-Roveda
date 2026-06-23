# MESOS

Software Engineering Project 2026

## Authors

- Mattia Zonzin
- Luca Pellegrini
- Anna Samarani
- Alberto Roveda

---

# Implemented Features

### Mandatory Features

- Complete game rules
- TUI (Text User Interface)
- GUI (Graphical User Interface)
- RMI communication
- Socket communication

### Additional Features

- **Match Ranking Database (MySQL)**  
  The server stores completed matches in a MySQL database, saving player nickname, final score, match date, and number of players. At the end of each match, players can view their ranking position and the complete leaderboard for matches with the same number of participants.

- **Multiple Concurrent Matches**  
  The server supports multiple games running simultaneously, allowing players to choose an existing waiting room or create a new match.

- **Persistence**  
  The server periodically saves the game state to disk, allowing interrupted matches to be restored after a server restart.

- **Disconnection Resilience**  
  Disconnected players can reconnect and continue their match. The game continues by skipping disconnected players' turns. If only one player remains connected, the game is suspended until another player reconnects.

---

# Running the Application

## Requirements

- Java 21 or later

---

## Server

To start the server, run:

```bash
java -jar Mesos-Server.jar
```

No command-line parameters are required.

At startup:

1. Insert the IP address of the machine running the server.
2. Insert the MySQL username and password to enable the ranking database.
3. Alternatively, press Enter to start the server without database support.

Once started, the server accepts:

- RMI connections on port **1099**
- Socket connections on port **1234**

---

## CLI Client

To start the CLI client, run:

```bash
java -jar Mesos-Client-CLI.jar
```

No command-line parameters are required.

At startup:

1. Insert the IP address of the server.
2. Insert the IP address of the machine running the client.
3. Choose the connection protocol:
   - RMI
   - Socket
4. Insert a unique nickname.

After completing these steps, the player enters the lobby.

---

## GUI Client

To start the GUI client, run:

```bash
java -jar Mesos-Client-GUI.jar
```

No command-line parameters are required.

At startup:

1. Insert a unique nickname.
2. Choose the connection protocol:
   - RMI
   - Socket
3. Insert the server IP address.
4. Insert the server port (pre-filled with the default value corresponding to the selected protocol).
5. Insert the IP address of the machine running the client.
6. Press the connection button.

After completing these steps, the player enters the lobby.

---

# Platform Compatibility

The provided JAR files for the **Server** and the **CLI Client** are fully platform-independent and can be executed on any operating system supported by Java 21 or later.

The **GUI Client** JAR includes JavaFX dependencies only for:

- Windows
- macOS (ARM architecture)

As a consequence, the GUI client can be executed directly only on these platforms using the provided JAR file.

Users running other operating systems may need to manually provide the appropriate JavaFX runtime libraries for their platform in order to launch the GUI client.
