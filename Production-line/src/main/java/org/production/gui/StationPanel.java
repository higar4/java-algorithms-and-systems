package org.production.gui;
import org.production.model.Cargo;
import org.production.model.Station;
import javax.swing.*;
import java.awt.*;

public class StationPanel extends JPanel {
    private final Station station;
    private final int CELL_SIZE = 50;
    private final int PADDING = 20;

    public StationPanel(Station station) {
        this.station = station;
        setPreferredSize(new Dimension(800, 300));
        setBackground(new Color(240, 240, 240));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Cargo[] inputs = station.getInputRampCopy();
        Cargo[] outputs = station.getOutputRampCopy();
        int[] positions = station.getLoaderPositionsCopy();
        Cargo[] loadersCargos = station.getLoaderCargosCopy();
        int startX = PADDING;
        int trackY = 120;
        int inputY = trackY - CELL_SIZE - 10;
        int outputY = trackY + CELL_SIZE + 10;
        drawRow(g2, inputs, station.inputOffset, inputY, new Color(200, 220, 255), "WEJŚCIE");
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(startX, trackY, station.trackLength * CELL_SIZE, CELL_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(startX, trackY, station.trackLength * CELL_SIZE, CELL_SIZE);
        for(int i=0; i<=station.trackLength; i++) {
            g2.drawLine(startX + i*CELL_SIZE, trackY, startX + i*CELL_SIZE, trackY + CELL_SIZE);
        }
        for (int i = 0; i < station.K; i++) {
            int pos = positions[i];
            int px = startX + pos * CELL_SIZE;
            g2.setColor(loadersCargos[i] == null ? new Color(255, 230, 100) : new Color(200, 150, 50));
            g2.fillRoundRect(px + 5, trackY + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(px + 5, trackY + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);
            if (loadersCargos[i] != null) {
                drawCargo(g2, loadersCargos[i], px, trackY);
            }
            g2.setColor(Color.BLACK);
            g2.drawString("P" + i, px + 5, trackY + 15);
        }
        drawRow(g2, outputs, station.outputOffset, outputY, new Color(200, 255, 200), "WYJŚCIE");
    }

    private void drawRow(Graphics2D g2, Cargo[] data, int offsetCells, int y, Color bgColor, String label) {
        int startX = PADDING + offsetCells * CELL_SIZE;
        g2.setColor(bgColor);
        g2.fillRect(startX, y, data.length * CELL_SIZE, CELL_SIZE);
        g2.setColor(Color.GRAY);
        g2.drawRect(startX, y, data.length * CELL_SIZE, CELL_SIZE);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(label, startX - 50, y + 30);
        for (int i = 0; i < data.length; i++) {
            int x = startX + i * CELL_SIZE;
            g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            if (data[i] != null) {
                drawCargo(g2, data[i], x, y);
            }
        }
    }

    private void drawCargo(Graphics2D g2, Cargo c, int x, int y) {
        g2.setColor(c.color());
        g2.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String text = String.valueOf(c.id());
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (CELL_SIZE - fm.stringWidth(text)) / 2;
        int ty = y + (CELL_SIZE + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, tx, ty);
    }
}