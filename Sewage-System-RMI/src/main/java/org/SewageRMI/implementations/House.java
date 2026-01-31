package org.SewageRMI.implementations;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ITailor;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class House implements IHouse {

    private final String myName;
    private final int capacity = 200;
    private int currentWaste = 0;
    private IOffice office;

    private JFrame window;
    private JProgressBar tankBar;
    private JTextArea logArea;
    private JButton btnProduce, btnOrder;

    public House(String name, IOffice office) {
        this.myName = name;
        this.office = office;
        setupGUI();
    }


    @Override
    public String getName() throws RemoteException {
        return myName;
    }

    @Override
    public synchronized int getPumpOut(int max) throws RemoteException {
        int pump = Math.min(currentWaste, max);
        currentWaste -= pump;

        SwingUtilities.invokeLater(this::updateView);
        log("-> Cysterna wypompowała: " + pump + "L");

        return pump;
    }


    private void setupGUI() {
        window = new JFrame("House: " + myName);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(350, 300);
        window.setLayout(new BorderLayout());

        tankBar = new JProgressBar(0, capacity);
        tankBar.setStringPainted(true);
        tankBar.setString("0 / " + capacity + "L");

        JPanel pBtns = new JPanel(new GridLayout(2, 1));
        btnProduce = new JButton("Produkuj Ścieki (+20L)");
        btnOrder = new JButton("Zamów Wywóz");

        btnProduce.addActionListener(e -> {
            currentWaste = Math.min(currentWaste + 20, capacity);
            updateView();
            log("Wyprodukowano ścieki. Stan: " + currentWaste);
        });

        btnOrder.addActionListener(e -> orderTransfer());

        pBtns.add(btnProduce);
        pBtns.add(btnOrder);

        logArea = new JTextArea();
        logArea.setEditable(false);

        window.add(tankBar, BorderLayout.NORTH);
        window.add(new JScrollPane(logArea), BorderLayout.CENTER);
        window.add(pBtns, BorderLayout.SOUTH);

        window.setLocation(100 + (int)(Math.random()*200), 300 + (int)(Math.random()*200));
    }

    private void updateView() {
        tankBar.setValue(currentWaste);
        tankBar.setString(currentWaste + "/" + capacity + "L");
        tankBar.setForeground(currentWaste > capacity * 0.8 ? Color.RED : Color.GREEN);
    }

    private void orderTransfer() {
        new Thread(() -> {
            try {
                log("Wysyłam zamówienie do biura...");
                int result = office.order(this);
                if (result == 1) log("Biuro przyjęło zamówienie.");
                else log("Biuro odrzuciło zamówienie.");

            } catch (RemoteException e) {
                log("Błąd komunikacji z biurem: " + e.getMessage());
            }
        }).start();
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
        String myName = args.length > 2 ? args[2] : "House1";
        String officeName = args.length > 3 ? args[3] : "Office1";

        try {
            Registry registry = LocateRegistry.getRegistry(tailorHost, tailorPort);
            ITailor tailor = (ITailor) registry.lookup("Tailor");
            System.out.println("Szukam biura: " + officeName + "...");
            IOffice office = null;
            while (office == null) {
                try {
                    office = (IOffice) tailor.getRemote(officeName);
                    if (office == null) Thread.sleep(1000);
                } catch (Exception e) { Thread.sleep(1000); }
            }
            System.out.println("Znaleziono Biuro!");
            House house = new House(myName, office);
            house.setVisible(true);

            UnicastRemoteObject.exportObject(house, 0);

            house.log("Dom gotowy. Połączono z: " + officeName);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd: " + e.getMessage());
        }
    }
}