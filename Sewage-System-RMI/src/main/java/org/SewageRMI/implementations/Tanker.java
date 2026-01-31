package org.SewageRMI.implementations;

import interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Tanker implements ITanker {

    private final String myName;
    private final int capacity = 500;
    private int currentLoad = 0;
    private int myId = -1;

    private IOffice office;
    private ISewagePlant plant;
    private IHouse currentJob;

    private JFrame window;
    private JLabel statusLabel;
    private JProgressBar loadBar;
    private JButton btnReg, btnReady, btnPump, btnDump;
    private JTextArea logArea;

    public Tanker(String name, IOffice office, ISewagePlant plant) {
        this.myName = name;
        this.office = office;
        this.plant = plant;
        setupGUI();
    }


    @Override
    public String getName() throws RemoteException {
        return myName;
    }

    @Override
    public void setJob(IHouse house) throws RemoteException {
        this.currentJob = house;
        String houseName = (house != null) ? house.getName() : "Nieznany";

        log("!!! Otrzymano zlecenie od Biura: " + houseName);

        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Zlecenie: " + houseName);
            btnPump.setEnabled(true);
            btnReady.setEnabled(false);
            btnDump.setEnabled(false);
        });
    }


    private void registerInOffice() {
        new Thread(() -> {
            try {
                myId = office.register(this);
                log("Zarejestrowano w biurze. Otrzymano ID: #" + myId);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("ID: #" + myId + " (Czekam na gotowość)");
                    btnReg.setEnabled(false);
                    btnReady.setEnabled(true);
                });
            } catch (RemoteException e) {
                log("Błąd rejestracji w biurze: " + e.getMessage());
            }
        }).start();
    }

    private void setReady() {
        new Thread(() -> {
            try {
                office.setReadyToServe(myId);
                log("Zgłoszono gotowość do Biura.");

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Czekam na zlecenie...");
                    btnReady.setEnabled(false);
                });
            } catch (RemoteException e) {
                log("Błąd zgłaszania gotowości: " + e.getMessage());
            }
        }).start();
    }

    private void pump() {
        new Thread(() -> {
            if (currentJob == null) return;
            try {
                int space = capacity - currentLoad;
                if (space <= 0) {
                    log("Cysterna pełna! Nie mogę pompować.");
                    return;
                }

                log("Pompuję ścieki z domu...");
                int pumped = currentJob.getPumpOut(space);

                currentLoad += pumped;
                log("Wypompowano: " + pumped + "L");

                SwingUtilities.invokeLater(() -> {
                    updateLoadBar();
                    btnPump.setEnabled(false);

                    if (currentLoad < capacity) {
                        btnReady.setEnabled(true);
                        btnDump.setEnabled(true);
                        statusLabel.setText("Zadanie wykonane.");
                    } else {
                        btnReady.setEnabled(false);
                        btnDump.setEnabled(true);
                        statusLabel.setText("PEŁNA! Wymagany zrzut.");
                    }
                });

                currentJob = null;

            } catch (RemoteException e) {
                log("Błąd pompowania: " + e.getMessage());
            }
        }).start();
    }

    private void dump() {
        new Thread(() -> {
            if (currentLoad == 0) return;
            try {
                log("Jadę do oczyszczalni...");
                plant.setPumpIn(myId, currentLoad);

                log("Zrzucono " + currentLoad + "L");
                currentLoad = 0;

                SwingUtilities.invokeLater(() -> {
                    updateLoadBar();
                    statusLabel.setText("Pusta. Zgłoś gotowość.");
                    btnDump.setEnabled(false);
                    btnReady.setEnabled(true);
                });

            } catch (RemoteException e) {
                log("Błąd zrzutu: " + e.getMessage());
            }
        }).start();
    }

    private void updateLoadBar() {
        loadBar.setValue(currentLoad);
        loadBar.setString(currentLoad + "/" + capacity + "L");
        loadBar.setForeground(currentLoad > capacity * 0.8 ? Color.RED : Color.GREEN);
    }

    private void setupGUI() {
        window = new JFrame("Tanker: " + myName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(350, 450);
        window.setLayout(new GridLayout(6, 1, 5, 5));

        statusLabel = new JLabel("Stan: Niezarejestrowany", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        window.add(statusLabel);

        loadBar = new JProgressBar(0, capacity);
        loadBar.setStringPainted(true);
        loadBar.setString("0 / " + capacity + "L");
        window.add(loadBar);

        btnReg = new JButton("1. Rejestracja (Biuro)");
        btnReady = new JButton("2. Zgłoś Gotowość");
        btnPump = new JButton("3. Pompuj (Dom)");
        btnDump = new JButton("4. Zrzut (Oczyszczalnia)");

        btnReady.setBackground(new Color(144, 238, 144));
        btnDump.setBackground(new Color(255, 160, 122));

        btnReady.setEnabled(false);
        btnPump.setEnabled(false);
        btnDump.setEnabled(false);

        btnReg.addActionListener(e -> registerInOffice());
        btnReady.addActionListener(e -> setReady());
        btnPump.addActionListener(e -> pump());
        btnDump.addActionListener(e -> dump());

        window.add(btnReg);
        window.add(btnReady);
        window.add(btnPump);
        window.add(btnDump);

        logArea = new JTextArea();
        logArea.setEditable(false);
        window.add(new JScrollPane(logArea));

        window.setLocation(50, 400);
    }

    public void setVisible(boolean v) {
        window.setVisible(v);
    }

    private void log(String s) {
        SwingUtilities.invokeLater(() -> logArea.append(s + "\n"));
    }


    public static void main(String[] args) {
        String tailorHost = args.length > 0 ? args[0] : "localhost";
        int tailorPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        String myName = args.length > 2 ? args[2] : "Tanker1";
        String officeName = args.length > 3 ? args[3] : "Office1";
        String plantName = args.length > 4 ? args[4] : "SewagePlant1";

        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor tailor = (ITailor) registry.lookup("Tailor");

            System.out.println("Szukam Biura (" + officeName + ") i Oczyszczalni (" + plantName + ")...");

            IOffice office = null;
            ISewagePlant plant = null;

            while (office == null || plant == null) {
                if (office == null) office = (IOffice) tailor.getRemote(officeName);
                if (plant == null) plant = (ISewagePlant) tailor.getRemote(plantName);
                if (office == null || plant == null) {
                    System.out.println("Czekam na systemy...");
                    Thread.sleep(1500);
                }
            }
            System.out.println("Znaleziono partnerów!");

            Tanker tanker = new Tanker(myName, office, plant);
            tanker.setVisible(true);

            UnicastRemoteObject.exportObject(tanker, 0);

            tanker.log("System gotowy.");
            tanker.log("Widzę Biuro: " + officeName);
            tanker.log("Widzę Oczyszczalnię: " + plantName);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd startu Cysterny: " + e.getMessage());
            System.exit(1);
        }
    }
}