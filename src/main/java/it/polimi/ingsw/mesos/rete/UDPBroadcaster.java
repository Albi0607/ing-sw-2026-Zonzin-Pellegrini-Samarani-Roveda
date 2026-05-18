package it.polimi.ingsw.mesos.rete;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class UDPBroadcaster implements Runnable {
    private final String serverIp;
    private final int socketPort;
    private final int rmiPort;
    private volatile boolean running = true;

    public UDPBroadcaster(String serverIp, int socketPort, int rmiPort) {
        this.serverIp = serverIp;
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
    }

    // Metodo per spegnere il broadcaster
    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            String message = "MESOS_SERVER:" + serverIp + ":" + socketPort + ":" + rmiPort;

            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

            System.out.println("📡 UDP Broadcaster attivo (Daemon): Segnalazione LAN in corso...");

            while (running) {
                for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (!ni.isUp() || ni.isLoopback()) continue;

                    for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                        InetAddress broadcast = ia.getBroadcast();
                        if (broadcast == null) continue;

                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, 8888);
                        try {
                            socket.send(packet);
                        } catch (java.net.NoRouteToHostException e) {
                            // L'interfaccia corrente blocca il broadcast. Lo ignoriamo e andiamo avanti.
                        }catch (java.net.PortUnreachableException e) {
                            // Il router rifiuta esplicitamente il traffico sulla porta. Ignoriamo.
                        } catch (java.net.SocketException e) {
                            // Caduta di rete temporanea sull'interfaccia o socket chiuso.
                        } catch (SecurityException e) {
                            // Windows Defender / Antivirus sta bloccando Java su questa scheda di rete.
                        } catch (Exception e) {
                            System.err.println("Errore di invio pacchetto: " + e.getMessage());
                        }
                    }
                }
                Thread.sleep(2000);
            }
            System.out.println("📡 UDP Broadcaster arrestato.");
        } catch (Exception e) {
            System.err.println("Errore nel UDP Broadcaster: " + e.getMessage());
        }
    }
}