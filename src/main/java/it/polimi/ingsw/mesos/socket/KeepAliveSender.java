package it.polimi.ingsw.mesos.socket;

import it.polimi.ingsw.mesos.socket.Message.messageClient.PingMessage;
import it.polimi.ingsw.mesos.socket.SocketVirtualView;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class KeepAliveSender implements Runnable {

    private static final long INTERVAL_MS = 5_000;
    private final ObjectOutputStream out;
    private volatile boolean running = true;

    public KeepAliveSender(ObjectOutputStream out) {
        this.out = out;
    }

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

    public void stop() { running = false; }
}