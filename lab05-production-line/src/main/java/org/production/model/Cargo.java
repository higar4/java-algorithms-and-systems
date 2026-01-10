package org.production.model;

import java.awt.Color;

public record Cargo(int id, Color color) {
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}