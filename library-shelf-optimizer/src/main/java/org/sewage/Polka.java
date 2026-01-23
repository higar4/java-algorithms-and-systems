package org.sewage;

import java.util.*;

public class Polka {
    int id;
    double wysokosc;
    double szerokosc;
    List<Ksiazka> ksiazki = new ArrayList<>();

    public Polka(int id, double wysokosc, double szerokosc) {
        this.id = id;
        this.wysokosc = wysokosc;
        this.szerokosc = szerokosc;
    }

    public boolean mozeDodac(Ksiazka k) {
        return k.wysokosc <= wysokosc && (sumaGrubosci() + k.grubosc <= szerokosc);
    }

    public void dodaj(Ksiazka k) {
        ksiazki.add(k);
    }

    public double sumaGrubosci() {
        return ksiazki.stream().mapToDouble(x -> x.grubosc).sum();
    }

    public int roznicaTematow(int liczbaWszystkichTematow) {
        Set<Integer> tematy = new HashSet<>();
        for (Ksiazka k : ksiazki) tematy.add(k.temat);
        return liczbaWszystkichTematow - tematy.size();
    }

    @Override
    public String toString() {
        return "Półka " + id + ": " + ksiazki;
    }
}
