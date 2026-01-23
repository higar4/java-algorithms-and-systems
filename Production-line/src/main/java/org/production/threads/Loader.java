package org.production.threads;
import org.production.model.Cargo;
import org.production.model.Station;
import java.util.Random;

public class Loader extends Thread {
    private final Station station;
    private final int id;
    private final Random random = new Random();
    private int stuckCounter = 0;
    private static final int STUCK_THRESHOLD = 5;
    private int dominancePatience = 0;
    private static final int PATIENCE_LIMIT = 10;
    private int currentCarryTime = 0;

    public Loader(Station station, int id) {
        this.station = station;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                updateMyUrgency();
                step();
                Thread.sleep(150 + random.nextInt(200));
            }
        } catch (InterruptedException e) { }
    }

    private void updateMyUrgency() {
        Cargo myCargo = station.getLoaderCargo(id);
        if (myCargo != null) {
            currentCarryTime++;
        } else {
            currentCarryTime = 0;
        }
        station.updateUrgency(id, currentCarryTime);
    }

    private void step() {
        Cargo myCargo = station.getLoaderCargo(id);
        int myPos = station.getLoaderPosition(id);
        if (myCargo != null) {
            int targetOutputIdx = myCargo.id() - 1;
            int targetTrackPos = station.outputOffset + targetOutputIdx;
            if (myPos == targetTrackPos) {
                if (station.tryDrop(id)) {
                    resetCounters();
                } else {
                    stuckCounter++;
                }
            } else {
                moveSmart(targetTrackPos);
            }
        } else {
            if (station.tryPickUp(id)) {
                resetCounters();
                return;
            }
            int targetInputPos = station.findNearestInputCargo(myPos);
            if (targetInputPos != -1) {
                moveSmart(targetInputPos);
            } else {
                int parkPos = id * (station.trackLength / station.K);
                moveSmart(parkPos);
            }
        }
    }

    private void resetCounters() {
        stuckCounter = 0;
        dominancePatience = 0;
        currentCarryTime = 0;
        station.updateUrgency(id, 0);
    }

    private void moveSmart(int targetPos) {
        int currentPos = station.getLoaderPosition(id);
        if (currentPos == targetPos) return;
        int desiredDirection = Integer.signum(targetPos - currentPos);
        int nextPos = currentPos + desiredDirection;
        Integer obstacleId = station.getLoaderIdAt(nextPos);
        if (obstacleId == null) {
            boolean moved = station.tryMoveLoader(id, desiredDirection);
            if (moved) {
                stuckCounter = 0;
                dominancePatience = 0;
            } else {
                stuckCounter++;
            }
        } else {
            resolveConflict(desiredDirection, obstacleId);
        }
    }

    private void resolveConflict(int desiredDirection, int otherId) {
        Cargo myCargo = station.getLoaderCargo(id);
        Cargo otherCargo = station.getLoaderCargo(otherId);
        boolean iAmFull = (myCargo != null);
        boolean heIsFull = (otherCargo != null);
        boolean iShouldYield = false;
        if (!iAmFull && heIsFull) {
            iShouldYield = true;
        }
        else if (iAmFull && heIsFull) {
            int myUrgency = this.currentCarryTime;
            int hisUrgency = station.getUrgency(otherId);
            if (myUrgency < hisUrgency) {
                iShouldYield = true;
            } else if (myUrgency == hisUrgency) {
                if (id > otherId) iShouldYield = true;
            }
        }
        else if (!iAmFull && !heIsFull) {
            if (id > otherId) iShouldYield = true;
        }
        if (iShouldYield) {
            int retreatDir = -desiredDirection;
            if (station.tryMoveLoader(id, retreatDir)) {
                stuckCounter = 0;
            } else {
                stuckCounter++;
            }
        } else {
            if (dominancePatience > PATIENCE_LIMIT) {
                int retreatDir = -desiredDirection;
                station.tryMoveLoader(id, retreatDir);
                if (random.nextDouble() < 0.3) dominancePatience = 0;
            } else {
                dominancePatience++;
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        }
    }
}