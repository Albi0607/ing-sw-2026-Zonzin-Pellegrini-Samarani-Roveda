package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.socket.Message.messageClient.PingMessage;
import it.polimi.ingsw.mesos.socket.SocketVirtualView;

import java.io.IOException;
import java.io.ObjectOutputStream;
/**
 * Periodically sends {@link PingMessage} objects to the server to keep the connection alive.
 *
 * <p>Runs on a dedicated daemon thread and sends a ping every {@value INTERVAL_MS} ms.
 * Stops automatically if the server becomes unreachable or if {@link #stop()} is called.
 */
public class KeepAliveSender implements Runnable {

    private static final long INTERVAL_MS = 5_000;
    private final ObjectOutputStream out;
    private volatile boolean running = true;

    /**
     * Constructs a {@code KeepAliveSender} that writes to the given output stream.
     *
     * @param out the {@link ObjectOutputStream} used to send ping messages to the server
     */
    public KeepAliveSender(ObjectOutputStream out) {
        this.out = out;
    }

    /**
     * Starts the keep-alive loop, sending a {@link PingMessage} to the server
     * at a fixed interval of {@value INTERVAL_MS} milliseconds.
     *
     * <p>The loop terminates if:
     * <ul>
     *   <li>an {@link IOException} occurs (server unreachable);</li>
     *   <li>the thread is interrupted;</li>
     *   <li>{@link #stop()} is called externally.</li>
     * </ul>
     */
    @Override
    public void run() {
        while (running) {
            try {
                synchronized (out) {
                    out.writeObject(new PingMessage());
                    out.flush();
                    out.reset();
                }
                Thread.sleep(INTERVAL_MS);
            } catch (IOException e) {
                System.err.println("[KeepAlive] Server irraggiungibile.");
                running = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Stops the keep-alive loop at the next iteration.
     */
    public void stop() { running = false; }
}