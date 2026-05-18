package it.polimi.ingsw.mesos.rete;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class ServerDiscoverer {

    public static String[] discoverServerInfo() {
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(8888));
            socket.setSoTimeout(5000);

            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                if (message.startsWith("MESOS_SERVER:")) {
                    String[] parts = message.split(":");

                    // Validazione del pacchetto (deve contenere Prefix, IP, SocketPort, RMIPort)
                    if (parts.length >= 4) {
                        return new String[]{parts[1], parts[2], parts[3]};
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}