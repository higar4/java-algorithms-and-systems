package org.production;
import org.production.threads.Annihilator;
import org.production.threads.Creator;
import org.production.threads.Loader;
import org.production.model.Station;
import org.production.gui.StationPanel;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        int X = 3;
        int Y = 6;
        int K = 4;
        System.out.println("Start symulacji: X=" + X + ", Y=" + Y + ", K=" + K);
        System.out.println("Długość toru: " + (Y + 2 * (K - 1)));
        Station station = new Station(X, Y, K);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Production Line");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            StationPanel panel = new StationPanel(station);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            new Timer(16, e -> panel.repaint()).start();
        });
        new Creator(station).start();
        new Annihilator(station).start();
        for (int i = 0; i < K; i++) {
            new Loader(station, i).start();
        }
    }
}