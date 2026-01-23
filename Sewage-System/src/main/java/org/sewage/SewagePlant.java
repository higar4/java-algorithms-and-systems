package org.sewage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class SewagePlant extends JFrame {
    private static final int myPort = 9001;
    private final ConcurrentHashMap<Integer, Integer> storage = new ConcurrentHashMap<>();
    private JTextArea logArea;
    private DefaultTableModel tableModel;

    public SewagePlant() {
        super("SewagePlant (Port " + myPort + ")");
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        String[] columns = {"ID Cysterny", "Do Zapłaty (L)"};
        tableModel = new DefaultTableModel(columns, 0);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
        setLocation(0, 0);
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(myPort)) {
                log("Oczyszczalnia czeka na zrzuty...");
                while (true) {
                    Socket client = server.accept();
                    handleRequest(client);
                }
            } catch (IOException e) { log("Błąd: " + e.getMessage()); }
        }).start();
    }

    private void handleRequest(Socket socket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String req = in.readLine();
                if (req == null) return;
                String[] parts = req.split(":");
                String cmd = parts[0];
                String[] args = parts.length > 1 ? parts[1].split(",") : new String[0];
                if (cmd.equals("spi")) {
                    int id = Integer.parseInt(args[0]);
                    int vol = Integer.parseInt(args[1]);
                    storage.merge(id, vol, Integer::sum);
                    refreshTable();
                    log("-> Przyjęto " + vol + "litrów od #" + id);
                    out.println("1");

                } else if (cmd.equals("gs")) {
                    int id = Integer.parseInt(args[0]);
                    out.println(storage.getOrDefault(id, 0));

                } else if (cmd.equals("spo")) {
                    int id = Integer.parseInt(args[0]);
                    int amount = storage.getOrDefault(id, 0);
                    storage.put(id, 0);
                    refreshTable();
                    log("$$$ Biuro opłaciło fakturę za cysternę #" + id + " (" + amount + "l)");
                    log("--- Stan licznika wyzerowany ---");
                    out.println("1");

                } else {
                    out.println("0");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            storage.forEach((k, v) -> tableModel.addRow(new Object[]{k, v}));
        });
    }

    private void log(String s) { SwingUtilities.invokeLater(() -> logArea.append(s + "\n")); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SewagePlant().setVisible(true));
    }
}