package org.production.threads;
import org.production.model.Cargo;
import org.production.model.Station;
import java.awt.Color;
import java.util.Random;

public class Creator extends Thread {
    private final Station station;
    private final Random random = new Random();
    private final Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
    public Creator(Station station) {
        this.station = station;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                int id = random.nextInt(station.Y) + 1;
                Color c = colors[(id - 1) % colors.length];
                station.addCargoToInput(new Cargo(id, c));
                Thread.sleep(800);
            }
        } catch (InterruptedException e) { }
    }
}