package org.sewage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Tanker extends JFrame {
    private static final int myPort = 7000;
    private final int officePort = 8000;
    private final int plantPort = 9001;
    private final int capacity = 500;
    private int myId = -1;
    private int currentLoad = 0;
    private String clientHost;
    private int clientPort;
    private JLabel statusLabel;
    private JProgressBar loadBar;
    private JButton btnReg, btnReady, btnPump, btnDump;
    private JTextArea logArea;

    public Tanker() {
        super("Tanker (Port " + myPort + ")");
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 450);
        setLayout(new GridLayout(6, 1, 5, 5));
        statusLabel = new JLabel("Stan: Nieznany", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel);
        loadBar = new JProgressBar(0, capacity);
        loadBar.setStringPainted(true);
        loadBar.setString("0 / " + capacity + "L");
        add(loadBar);
        btnReg = new JButton("1. Rejestracja (Biuro)");
        btnReady = new JButton("2. Zgłoś Gotowość");
        btnPump = new JButton("3. Pompuj (Klient)");
        btnDump = new JButton("4. Zrzut (Oczyszczalnia)");
        btnReady.setBackground(new Color(144, 238, 144));
        btnDump.setBackground(new Color(255, 160, 122));
        btnReady.setEnabled(false);
        btnPump.setEnabled(false);
        btnDump.setEnabled(false);
        btnReg.addActionListener(e -> register());
        btnReady.addActionListener(e -> setReady());
        btnPump.addActionListener(e -> pump());
        btnDump.addActionListener(e -> dump());
        add(btnReg);
        add(btnReady);
        add(btnPump);
        add(btnDump);
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea));
        setLocation(0, 400);
    }


    private void register() {
        new Thread(() -> {
            String res = SocketUtil.sendRequest("localhost", officePort, "r:" + myPort + ",localhost");
            if (!res.equals("0")) {
                myId = Integer.parseInt(res);
                log("Nadano ID: " + myId);
                SwingUtilities.invokeLater(() -> {
                    btnReg.setEnabled(false);
                    btnReady.setEnabled(true);
                    statusLabel.setText("ID: #" + myId);
                });
            }
        }).start();
    }

    private void setReady() {
        new Thread(() -> {
            SocketUtil.sendRequest("localhost", officePort, "sr:" + myId);
            SwingUtilities.invokeLater(() -> {
                btnReady.setEnabled(false);
                btnDump.setEnabled(false);
                statusLabel.setText("Czekam na zlecenie...");
            });
        }).start();
    }

    private void pump() {
        new Thread(() -> {
            int space = capacity - currentLoad;
            if (space <= 0) {
                log("Brak miejsca w zbiorniku!");
                return;
            }
            String res = SocketUtil.sendRequest(clientHost, clientPort, "gp:" + space);
            int pumped = Integer.parseInt(res);
            currentLoad += pumped;
            SwingUtilities.invokeLater(() -> {
                loadBar.setValue(currentLoad);
                loadBar.setString(currentLoad + "/" + capacity + "L");
                btnPump.setEnabled(false);
                if (currentLoad < capacity) {
                    btnReady.setEnabled(true);
                    btnDump.setEnabled(true);
                    statusLabel.setText("Decyzja: Gotowość czy Zrzut?");
                    log("Wolne miejsce: " + (capacity - currentLoad) + "L");
                } else {
                    btnReady.setEnabled(false);
                    btnDump.setEnabled(true);
                    statusLabel.setText("PEŁNY! Potrzebny zrzut");
                    loadBar.setForeground(Color.RED);
                }
            });
            log("Pobrano " + pumped + "L");
        }).start();
    }

    private void dump() {
        new Thread(() -> {
            if (currentLoad == 0) {
                log("Nie ma co zrzucać.");
                return;
            }
            SocketUtil.sendRequest("localhost", plantPort, "spi:" + myId + "," + currentLoad);
            currentLoad = 0;
            SwingUtilities.invokeLater(() -> {
                loadBar.setValue(0);
                loadBar.setString("0/" + capacity + "L");
                loadBar.setForeground(null);
                btnDump.setEnabled(false);
                btnReady.setEnabled(true);
                statusLabel.setText("Pusty -> Zgłoś gotowość");
            });
            log("Zrzut wykonany");
        }).start();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket s = new ServerSocket(myPort)) {
                while (true) {
                    Socket c = s.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    PrintWriter out = new PrintWriter(c.getOutputStream(), true);
                    String req = in.readLine();
                    out.println("1");
                    if (req.startsWith("sj:")) {
                        String[] args = req.split(":")[1].split(",");
                        clientPort = Integer.parseInt(args[0]);
                        clientHost = args[1];
                        log("Zlecenie: " + clientHost + ":" + clientPort);
                        SwingUtilities.invokeLater(() -> {
                            btnPump.setEnabled(true);
                            btnReady.setEnabled(false);
                            btnDump.setEnabled(false);
                            statusLabel.setText("Zlecenie przyjęte");
                        });
                    }
                }
            } catch (Exception e) {}
        }).start();
    }

    private void log(String s) { SwingUtilities.invokeLater(() -> logArea.append(s + "\n")); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tanker().setVisible(true));
    }
}