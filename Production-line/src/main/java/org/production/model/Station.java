package org.production.model;

public class Station {
    public final int X;
    public final int Y;
    public final int K;
    public final int trackLength;
    public final int outputOffset;
    public final int inputOffset;
    private final Cargo[] inputRamp;
    private final Cargo[] outputRamp;
    private final int[] loaderPositions;
    private final Cargo[] loaderCargos;
    private final int[] loaderUrgency;

    public Station(int X, int Y, int K) {
        this.X = X;
        this.Y = Y;
        this.K = K;
        this.trackLength = Y + 2 * (K - 1);
        this.outputOffset = K - 1;
        this.inputOffset = (trackLength - X) / 2;
        this.inputRamp = new Cargo[X];
        this.outputRamp = new Cargo[Y];
        this.loaderPositions = new int[K];
        this.loaderCargos = new Cargo[K];
        this.loaderUrgency = new int[K];
        for (int i = 0; i < K; i++) {
            loaderPositions[i] = i * (trackLength / K);
        }
    }

    public synchronized void addCargoToInput(Cargo cargo) {
        for (int i = 0; i < X; i++) {
            if (inputRamp[i] == null) {
                inputRamp[i] = cargo;
                notifyAll();
                return;
            }
        }
    }

    public synchronized void removeCargoFromOutput() {
        for (int i = 0; i < Y; i++) {
            if (outputRamp[i] != null) {
                outputRamp[i] = null;
                notifyAll();
                return;
            }
        }
    }

    public synchronized boolean tryMoveLoader(int loaderId, int direction) {
        int currentPos = loaderPositions[loaderId];
        int newPos = currentPos + direction;
        if (newPos < 0 || newPos >= trackLength) return false;
        if (loaderId > 0) {
            if (newPos <= loaderPositions[loaderId - 1]) return false;
        }
        if (loaderId < K - 1) {
            if (newPos >= loaderPositions[loaderId + 1]) return false;
        }
        loaderPositions[loaderId] = newPos;
        notifyAll();
        return true;
    }

    public synchronized boolean tryPickUp(int loaderId) {
        if (loaderCargos[loaderId] != null) return false;
        int myTrackPos = loaderPositions[loaderId];
        int inputIndex = myTrackPos - inputOffset;
        if (inputIndex >= 0 && inputIndex < X) {
            if (inputRamp[inputIndex] != null) {
                loaderCargos[loaderId] = inputRamp[inputIndex];
                inputRamp[inputIndex] = null;
                notifyAll();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean tryDrop(int loaderId) {
        Cargo c = loaderCargos[loaderId];
        if (c == null) return false;
        int myTrackPos = loaderPositions[loaderId];
        int targetOutputIdx = c.id() - 1;
        int targetTrackPos = outputOffset + targetOutputIdx;
        if (myTrackPos == targetTrackPos) {
            if (outputRamp[targetOutputIdx] == null) {
                outputRamp[targetOutputIdx] = c;
                loaderCargos[loaderId] = null;
                notifyAll();
                return true;
            }
        }
        return false;
    }

    public synchronized Integer getLoaderIdAt(int position) {
        for (int i = 0; i < K; i++) {
            if (loaderPositions[i] == position) return i;
        }
        return null;
    }

    public synchronized void updateUrgency(int loaderId, int urgencyLevel) {
        loaderUrgency[loaderId] = urgencyLevel;
    }

    public synchronized int getUrgency(int loaderId) {
        return loaderUrgency[loaderId];
    }

    public synchronized int findNearestInputCargo(int myPos) {
        int bestDist = Integer.MAX_VALUE;
        int targetPos = -1;
        for (int i = 0; i < X; i++) {
            if (inputRamp[i] != null) {
                int rampPosOnTrack = inputOffset + i;
                int dist = Math.abs(rampPosOnTrack - myPos);
                if (dist < bestDist) {
                    bestDist = dist;
                    targetPos = rampPosOnTrack;
                }
            }
        }
        return targetPos;
    }

    public synchronized int getLoaderPosition(int loaderId) { return loaderPositions[loaderId]; }
    public synchronized Cargo getLoaderCargo(int loaderId) { return loaderCargos[loaderId]; }
    public synchronized Cargo[] getInputRampCopy() { return inputRamp.clone(); }
    public synchronized Cargo[] getOutputRampCopy() { return outputRamp.clone(); }
    public synchronized int[] getLoaderPositionsCopy() { return loaderPositions.clone(); }
    public synchronized Cargo[] getLoaderCargosCopy() { return loaderCargos.clone(); }
}