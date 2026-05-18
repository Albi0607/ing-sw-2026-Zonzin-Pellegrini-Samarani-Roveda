package it.polimi.ingsw.mesos.RMI;

import java.rmi.RemoteException;

// gestisce il messaggi di KeepALive
public class KeepAliveRMI implements Runnable {

    private static final long INTERVAL_MS = 5_000;
    private final RemoteMethods remote;
    private final String nickname;
    private volatile boolean running = true;

    public KeepAliveRMI(RemoteMethods remote, String nickname) {
        this.remote = remote;
        this.nickname = nickname;
    }

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