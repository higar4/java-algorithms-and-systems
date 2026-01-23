package org.sewage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class House extends JFrame {
    private static final int myPort = 6000;
    private final int officePort = 8000;
    private final int capacity = 200;
    private int currentWaste = 0;
    private JProgressBar tankBar;
    private JTextArea logArea;
    private JButton btnProduce, btnOrder;

    public House() {
        super("House (Port " + myPort + ")");
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 300);
        setLayout(new BorderLayout());
        tankBar = new JProgressBar(0, capacity);
        tankBar.setStringPainted(true);
        tankBar.setString("0 / " + capacity + "L");
        JPanel pBtns = new JPanel(new GridLayout(2, 1));
        btnProduce = new JButton("Produkuj Ścieki (+20L)");
        btnOrder = new JButton("Zamów Wywóz");
        btnProduce.addActionListener(e -> {
            currentWaste = Math.min(currentWaste + 20, capacity);
            updateView();
            log("Wyprodukowano");
        });

        btnOrder.addActionListener(e -> {
            new Thread(() -> {
                String r = SocketUtil.sendRequest("localhost", officePort, "o:" + myPort + ",localhost");
                log(r.equals("1") ? "Zamówiono" : "Błąd.");
            }).start();
        });
        pBtns.add(btnProduce);
        pBtns.add(btnOrder);
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logArea);
        add(tankBar, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(pBtns, BorderLayout.SOUTH);
        setLocation(400, 400);
    }

    private void updateView() {
        tankBar.setValue(currentWaste);
        tankBar.setString(currentWaste + "/" + capacity + "L");
        tankBar.setForeground(currentWaste > capacity * 0.8 ? Color.RED : Color.GREEN);
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket s = new ServerSocket(myPort)) {
                while (true) {
                    Socket c = s.accept();
                    handleTanker(c);
                }
            } catch (Exception e) {}
        }).start();
    }

    private void handleTanker(Socket c) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
             PrintWriter out = new PrintWriter(c.getOutputStream(), true)) {
            String req = in.readLine();
            if (req != null && req.startsWith("gp:")) {
                int max = Integer.parseInt(req.split(":")[1]);
                int pump = Math.min(currentWaste, max);
                currentWaste -= pump;
                SwingUtilities.invokeLater(this::updateView);
                out.println(pump);
                log("Oddano " + pump + "L");
            }
        } catch (Exception e) {}
    }

    private void log(String s) {
        SwingUtilities.invokeLater(() -> logArea.append(s + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new House().setVisible(true));
    }
}