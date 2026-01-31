package org.SewageRMI.implementations;

import interfaces.ISewagePlant;
import interfaces.ITailor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class SewagePlant implements ISewagePlant {

    private final String myName;
    private final ConcurrentHashMap<Integer, Integer> storage = new ConcurrentHashMap<>();
    private JFrame window;
    private JTextArea logArea;
    private DefaultTableModel tableModel;

    public SewagePlant(String name) {
        this.myName = name;
        setupGUI();
    }

    @Override
    public String getName() throws RemoteException {
        return myName;
    }

    @Override
    public void setPumpIn(int number, int volume) throws RemoteException {
        storage.merge(number, volume, Integer::sum);
        refreshTable();
        log("-> Przyjęto " + volume + " litrów od cysterny #" + number);
    }

    @Override
    public int getStatus(int number) throws RemoteException {
        return storage.getOrDefault(number, 0);
    }

    @Override
    public void setPayoff(int number) throws RemoteException {
        int amount = storage.getOrDefault(number, 0);
        storage.put(number, 0);
        refreshTable();
        log("$$$ Biuro opłaciło fakturę za cysternę #" + number + " (" + amount + "l)");
        log("--- Stan licznika wyzerowany ---");
    }


    private void setupGUI() {
        window = new JFrame("SewagePlant: " + myName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400, 400);
        window.setLayout(new BorderLayout());

        String[] columns = {"ID Cysterny", "Do Zapłaty (L)"};
        tableModel = new DefaultTableModel(columns, 0);
        window.add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        window.add(new JScrollPane(logArea), BorderLayout.SOUTH);

        window.setLocation(800, 100);
    }

    public void setVisible(boolean visible) {
        window.setVisible(visible);
    }

    private void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            storage.forEach((k, v) -> tableModel.addRow(new Object[]{k, v}));
        });
    }

    private void log(String s) {
        SwingUtilities.invokeLater(() -> logArea.append(s + "\n"));
    }


    public static void main(String[] args) {
        String tailorHost = args.length > 0 ? args[0] : "localhost";
        int tailorPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        String myName = args.length > 2 ? args[2] : "SewagePlant1";

        try {
            SewagePlant plant = new SewagePlant(myName);
            plant.setVisible(true);

            ISewagePlant stub = (ISewagePlant) UnicastRemoteObject.exportObject(plant, 0);
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor tailor = (ITailor) registry.lookup("Tailor");
            tailor.register(myName, stub);

            plant.log("SYSTEM GOTOWY.");
            plant.log("Zarejestrowano u Krawca (" + tailorHost + ":" + tailorPort + ") jako: " + myName);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd: " + e.getMessage());
            System.exit(1);
        }
    }
}