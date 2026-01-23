package org.sewage;

import java.io.*;
import java.net.Socket;

public class SocketUtil {

    public static String sendRequest(String host, int port, String message) {
        System.out.println("[OUT -> " + port + "] " + message);
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(message);
            String response = in.readLine();
            System.out.println("[IN  <- " + port + "] " + response);
            return response != null ? response : "0";
        } catch (IOException e) {
            System.err.println("!!! Błąd połączenia z " + host + ":" + port + " (" + e.getMessage() + ")");
            return "0";
        }
    }
}