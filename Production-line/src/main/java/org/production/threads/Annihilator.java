package org.production.threads;
import org.production.model.Station;

public class Annihilator extends Thread {
    private final Station station;

    public Annihilator(Station station) {
        this.station = station;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                station.removeCargoFromOutput();
                Thread.sleep(1200);
            }
        } catch (InterruptedException e) { }
    }
}