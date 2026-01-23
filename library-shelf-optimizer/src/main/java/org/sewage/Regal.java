package org.sewage;

import java.util.*;

public class Regal {
    List<Polka> polki = new ArrayList<>();

    public Regal(List<Polka> polki) {
        this.polki = polki;
    }

    public double ocen(double wf, double wc, double wd, int liczbaWszystkichTematow) {
        double F = polki.stream().mapToDouble(Polka::sumaGrubosci).sum();
        double C = polki.stream().mapToInt(p -> p.ksiazki.size()).sum();
        double D = polki.stream().mapToInt(p -> p.roznicaTematow(liczbaWszystkichTematow)).sum();
        return wf * F + wc * C + wd * D;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Polka p : polki) sb.append(p).append("\n");
        return sb.toString();
    }
}
