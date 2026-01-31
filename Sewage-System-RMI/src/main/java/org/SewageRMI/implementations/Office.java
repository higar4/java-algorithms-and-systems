package org.SewageRMI.implementations;

import interfaces.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
public class Office implements IOffice {

    private final String myName;
    private ITailor tailor;
    private ISewagePlant plant;
    private final Map<Integer, ITanker> tankers = new HashMap<>();
    private final Map<String, IHouse> pendingJobs = new HashMap<>();
    private int idCounter = 1;

    private JFrame window;
    private DefaultListModel<String> tankersModel = new DefaultListModel<>();
    private DefaultListModel<String> jobsModel = new DefaultListModel<>();
    private JList<String> listTankers;
    private JList<String> listJobs;
    private JTextArea logArea;

    public Office(String name, ITailor tailor, ISewagePlant plant) {
        this.myName = name;
        this.tailor = tailor;
        this.plant = plant;
        setupGUI();
    }


    @Override
    public String getName() throws RemoteException {
        return myName;
    }

    @Override
    public synchronized int register(ITanker tanker) throws RemoteException {
        int newId = idCounter++;
        tankers.put(newId, tanker);
        String tankerName = tanker.getName();
        log("Zarejestrowano cysternę: " + tankerName + " (ID: " + newId + ")");
        return newId;
    }

    @Override
    public synchronized int order(IHouse house) throws RemoteException {
        String houseName = house.getName();
        pendingJobs.put(houseName, house);
        SwingUtilities.invokeLater(() -> jobsModel.addElement(houseName));
        log("Nowe zlecenie od: " + houseName);
        return 1;
    }

    @Override
    public synchronized void setReadyToServe(int number) throws RemoteException {
        checkPayoff(number);
        if (tankers.containsKey(number)) {
            ITanker t = tankers.get(number);
            String entry = "Cysterna #" + number + " [" + t.getName() + "]";
            SwingUtilities.invokeLater(() -> {
                if (!tankersModel.contains(entry)) tankersModel.addElement(entry);
            });
            log("Cysterna #" + number + " gotowa.");
        }
    }


    private void checkPayoff(int tankerId) {
        try {
            int debt = plant.getStatus(tankerId);
            if (debt > 0) {
                log("!!! Wykryto dług (" + debt + "L) cysterny #" + tankerId);
                plant.setPayoff(tankerId);
                log("-> Opłacono.");
            }
        } catch (RemoteException e) {
            log("Błąd Oczyszczalni: " + e.getMessage());
        }
    }

    private void assignJob() {
        String selTankerStr = listTankers.getSelectedValue();
        String selJobHouseName = listJobs.getSelectedValue();
        if (selTankerStr == null || selJobHouseName == null) return;

        try {
            int hashIndex = selTankerStr.indexOf("#");
            int spaceIndex = selTankerStr.indexOf(" ", hashIndex);
            int tId = Integer.parseInt(selTankerStr.substring(hashIndex + 1, spaceIndex));

            ITanker tanker = tankers.get(tId);
            IHouse house = pendingJobs.get(selJobHouseName);

            if (tanker != null && house != null) {
                new Thread(() -> {
                    try {
                        log("-> Wysyłam #" + tId + " do " + house.getName());
                        tanker.setJob(house);
                        SwingUtilities.invokeLater(() -> {
                            jobsModel.removeElement(selJobHouseName);
                            tankersModel.removeElement(selTankerStr);
                            pendingJobs.remove(selJobHouseName);
                        });
                    } catch (RemoteException e) {
                        log("Błąd zlecenia: " + e.getMessage());
                    }
                }).start();
            }
        } catch (Exception e) {
            log("Błąd: " + e.getMessage());
        }
    }


    private void setupGUI() {
        window = new JFrame("Office: " + myName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(600, 350);
        window.setLayout(new BorderLayout());

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
        window.add(lists, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JButton btnAssign = new JButton("PRZYDZIEL ZLECENIE");
        btnAssign.setBackground(new Color(144, 238, 144));
        btnAssign.addActionListener(e -> assignJob());
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);

        bottom.add(btnAssign, BorderLayout.NORTH);
        bottom.add(new JScrollPane(logArea), BorderLayout.CENTER);
        window.add(bottom, BorderLayout.SOUTH);

        window.setLocation(400, 0);
    }

    public void setVisible(boolean visible) {
        window.setVisible(visible);
    }

    private void log(String s) {
        SwingUtilities.invokeLater(() -> logArea.append(s + "\n"));
    }


    public static void main(String[] args) {
        String tailorHost = args.length > 0 ? args[0] : "localhost";
        int tailorPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        String myName = args.length > 2 ? args[2] : "Office1";
        String plantName = args.length > 3 ? args[3] : "SewagePlant1";

        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor tailor = (ITailor) registry.lookup("Tailor");

            Remote remotePlant = null;
            System.out.println("Szukam oczyszczalni...");
            while (remotePlant == null) {
                remotePlant = tailor.getRemote(plantName);
                if (remotePlant == null) Thread.sleep(1000);
            }
            ISewagePlant plant = (ISewagePlant) remotePlant;

            Office office = new Office(myName, tailor, plant);
            office.setVisible(true);

            IOffice stub = (IOffice) UnicastRemoteObject.exportObject(office, 0);
            tailor.register(myName, stub);

            office.log("Biuro gotowe: " + myName);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}