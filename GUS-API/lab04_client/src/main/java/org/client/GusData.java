package org.client;

public record GusData(String regionName, double value, int year) {
    @Override
    public String toString() {
        return regionName + ": " + value + " PLN (" + year + ")";
    }
}