package org.example;

public class Ksiazka {
    int id;
    double wysokosc;
    double grubosc;
    int temat;

    public Ksiazka(int id, double wysokosc, double grubosc, int temat) {
        this.id = id;
        this.wysokosc = wysokosc;
        this.grubosc = grubosc;
        this.temat = temat;
    }

    @Override
    public String toString() {
        return "Ksiazka{" + id + ", h=" + wysokosc + ", g=" + grubosc + ", t=" + temat + "}";
    }
}
