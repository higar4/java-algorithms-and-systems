package org.sewage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Office extends JFrame {
    private static final int myPort = 8000;
    private final int plantPort = 9001;
    private final String plantHost = "localhost";
    private DefaultListModel<String> tankersModel = new DefaultListModel<>();
    private DefaultListModel<String> jobsModel = new DefaultListModel<>();
    private Map<Integer, String> tankerAddresses = new HashMap<>();
    private int idCounter = 1;
    private JList<String> listTankers;
    private JList<String> listJobs;
    private JTextArea logArea;

    public Office() {
        super("Office (Port " + myPort + ")");
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 350);
        setLayout(new BorderLayout());
        JPanel lists = new JPanel(new GridLayout(1, 2));
        JPanel pTankers = new JPanel(new BorderLayout());
        pTankers.setBorder(BorderFactory.createTitledBorder("Wolne Cysterny"));
        listTankers = new JList<>(tankersModel);
        pTankers.add(new JScrollPane(listTankers));
        JPanel pJobs = new JPanel(new BorderLayout());
        pJobs.setBorder(BorderFactory.createTitledBorder("Zlecenia"));
        listJobs = new JList<>(jobsModel);
        pJobs.add(new JScrollPane(listJobs));
        lists.add(pTankers);
        lists.add(pJobs);
        add(lists, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        JButton btnAssign = new JButton("PRZYDZIEL ZLECENIE");
        btnAssign.setBackground(new Color(144, 238, 144));
        btnAssign.addActionListener(e -> assignJob());
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        bottom.add(btnAssign, BorderLayout.NORTH);
        bottom.add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setLocation(400, 0);
    }

    private void assignJob() {
        String selTanker = listTankers.getSelectedValue();
        String selJob = listJobs.getSelectedValue();
        if (selTanker == null || selJob == null) return;
        int tId = Integer.parseInt(selTanker.split("#")[1].split(" ")[0]);
        String tAddr = tankerAddresses.get(tId);
        String tHost = tAddr.split(":")[0];
        int tPort = Integer.parseInt(tAddr.split(":")[1]);
        String jobAddr = selJob.split(" ")[1];
        String hHost = jobAddr.split(":")[0];
        String hPort = jobAddr.split(":")[1];
        new Thread(() -> {
            log("-> Wysyłam cysternę #" + tId + " do " + jobAddr);
            SocketUtil.sendRequest(tHost, tPort, "sj:" + hPort + "," + hHost);
            SwingUtilities.invokeLater(() -> {
                jobsModel.removeElement(selJob);
                tankersModel.removeElement(selTanker);
            });
        }).start();
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(myPort)) {
                log("Biuro otwarte...");
                while (true) {
                    Socket client = server.accept();
                    handleRequest(client);
                }
            } catch (IOException e) { e.printStackTrace(); }
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
                if (cmd.equals("r")) {
                    int id = idCounter++;
                    tankerAddresses.put(id, args[1] + ":" + args[0]);
                    log("Rejestracja cysterny #" + id);
                    out.println(id);
                } else if (cmd.equals("sr")) {
                    int id = Integer.parseInt(args[0]);
                    checkPayoff(id);
                    SwingUtilities.invokeLater(() -> {
                        String entry = "Cysterna #" + id + " (" + tankerAddresses.get(id) + ")";
                        if (!tankersModel.contains(entry)) tankersModel.addElement(entry);
                    });
                    log("Cysterna #" + id + " gotowa");
                    out.println("1");
                } else if (cmd.equals("o")) {
                    String job = "Dom " + args[1] + ":" + args[0];
                    SwingUtilities.invokeLater(() -> jobsModel.addElement(job));
                    log("Nowe zlecenie: " + job);
                    out.println("1");
                } else {
                    out.println("0");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void checkPayoff(int id) {
        String res = SocketUtil.sendRequest(plantHost, plantPort, "gs:" + id);
        try {
            int vol = Integer.parseInt(res);
            if (vol > 0) {
                log("--------------------------------------------------");
                log("$$$ Znaleziono " + vol + "l w oczyszczalni od #" + id);
                log("$$$ Wysyłam polecenie zapłaty");
                SocketUtil.sendRequest(plantHost, plantPort, "spo:" + id);
                log("$$$ Opłacono");
                log("--------------------------------------------------");
            }
        } catch (NumberFormatException e) {
            log("Błąd odczytu z oczyszczalni");
        }
    }

    private void log(String s) { SwingUtilities.invokeLater(() -> logArea.append(s + "\n")); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Office().setVisible(true));
    }
}