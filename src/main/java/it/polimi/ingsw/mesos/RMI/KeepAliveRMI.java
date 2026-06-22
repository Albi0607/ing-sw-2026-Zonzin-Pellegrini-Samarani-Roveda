package it.polimi.ingsw.mesos.RMI;

import java.rmi.RemoteException;

/**
 * Manages the keep-alive mechanism for RMI connections by periodically
 * sending heartbeat signals to the remote server.
 *
 * <p>This class implements {@link Runnable} and is intended to be executed
 * in a dedicated background thread. It sends a heartbeat to the server
 * at fixed intervals to signal that the client is still alive and connected.
 * If the server becomes unreachable, the loop is terminated automatically.</p>
 */
public class KeepAliveRMI implements Runnable {

    private static final long INTERVAL_MS = 5_000;
    private final RemoteMethods remote;
    private final String nickname;
    private volatile boolean running = true;

    public KeepAliveRMI(RemoteMethods remote, String nickname) {
        this.remote = remote;
        this.nickname = nickname;
    }

    /**
     * Starts the keep-alive loop, sending a heartbeat to the server every
     * {@value #INTERVAL_MS} milliseconds.
     *
     * <p>The loop continues until one of the following occurs:</p>
     * <ul>
     *   <li>{@link #stop()} is called, setting {@code running} to {@code false}</li>
     *   <li>A {@link RemoteException} is thrown, indicating the server is unreachable</li>
     *   <li>The thread is interrupted externally</li>
     * </ul>
     *
     * <p>If a {@link RemoteException} is caught, an error message is printed to
     * {@code stderr} and the loop exits gracefully. If an {@link InterruptedException}
     * is caught, the thread's interrupt status is restored before exiting.</p>
     */
    @Override
    public void run() {
        while (running) {
            try {
                remote.heartbeat(nickname);
                Thread.sleep(INTERVAL_MS);
            } catch (RemoteException e) {
                System.err.println("[KeepAliveRMI] Server irraggiungibile.");
                running = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() { running = false; }
}