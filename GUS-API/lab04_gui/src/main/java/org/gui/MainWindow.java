/* KOMENDA DO URUCHOMIENIA W CMD:
java --module-path "lab04_client\target\lab04_client-1.0-SNAPSHOT.jar;lab04_gui\target\lab04_gui-1.0-SNAPSHOT.jar;C:\Users\Lenovo\.m2\repository\org\json\json\20231013\json-20231013.jar" --module lab04.gui/org.gui.MainWindow
 */

package org.gui;

import org.client.GusClient;
import org.client.GusData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

record GusTopic(String name, int id, String unitColumn) {
    @Override
    public String toString() { return name; }
}

public class MainWindow extends JFrame {
    private final GusClient client;
    private final DefaultTableModel tableModel;
    private final JComboBox<Integer> yearSelector;
    private final JComboBox<GusTopic> topicSelector;

    public MainWindow() {
        super("Przeglądarka Danych GUS");
        this.client = new GusClient();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Wyrównanie do lewej
        topPanel.add(new JLabel("Temat:"));
        topicSelector = new JComboBox<>(new GusTopic[]{
                new GusTopic("Przestępstwa stwierdzone przez Policję ogółem na 1000 mieszkańców",398594,"Liczba przestępstw"),
                new GusTopic("Miesięczne wynagrodzenie brutto na 1 zatrudnionego",196229,"PLN"),
                new GusTopic("ludność zameldowana na pobyt czasowy", 99719, "Liczba osób"),
                new GusTopic("Samochody osobowe", 32561, "Sztuki"),
                new GusTopic("Dobowa produkcja wody", 1621930, "Metry sześcienne")
        });
        topPanel.add(topicSelector);
        topPanel.add(new JLabel("Rok:"));
        yearSelector = new JComboBox<>(new Integer[]{2023, 2022, 2021, 2020, 2019, 2018});
        topPanel.add(yearSelector);
        JButton loadButton = new JButton("Pobierz dane");
        loadButton.addActionListener(e -> loadData());
        topPanel.add(loadButton);
        add(topPanel, BorderLayout.NORTH);
        String[] columns = {"Województwo", "Wartość", "Rok"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel panelDane = new JPanel(new BorderLayout());
        panelDane.add(topPanel, BorderLayout.NORTH);
        panelDane.add(new JScrollPane(table), BorderLayout.CENTER);
        tabbedPane.addTab("Dane GUS", panelDane);
        JPanel panelInfo = new JPanel();
        panelInfo.add(new JLabel("<html><h2>GUS Viewer v1.0</h2><p>Autor: Piotr Francuz</p></html>"));
        tabbedPane.addTab("O Programie", panelInfo);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadData() {
        int selectedYear = (int) yearSelector.getSelectedItem();
        GusTopic selectedTopic = (GusTopic) topicSelector.getSelectedItem();
        if (selectedTopic == null) return;
        String[] newColumns = {"Województwo", selectedTopic.unitColumn(), "Rok"};
        tableModel.setColumnIdentifiers(newColumns);
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));
                List<GusData> data = client.fetchData(selectedTopic.id(), selectedYear);
                SwingUtilities.invokeLater(() -> {
                    for (GusData d : data) {
                        tableModel.addRow(new Object[]{d.regionName(), d.value(), d.year()});
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Błąd: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}