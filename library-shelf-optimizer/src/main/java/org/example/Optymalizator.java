package org.example;

import java.util.*;

public class Optymalizator {

    public static Regal optymalizuj(List<Polka> polki, List<Ksiazka> ksiazki,
                                    double wf, double wc, double wd) {
        Regal najlepszy = new Regal(polki);

        int liczbaTematow = (int) ksiazki.stream().map(k -> k.temat).distinct().count();

        ksiazki.sort(Comparator.comparingDouble((Ksiazka k) -> k.wysokosc)
                .thenComparingInt(k -> k.temat));

        for (Ksiazka k : ksiazki) {
            Polka najlepsza = null;
            double najlepszyWynik = Double.NEGATIVE_INFINITY;

            for (Polka p : polki) {
                if (p.mozeDodac(k)) {
                    p.dodaj(k);
                    double ocena = new Regal(polki).ocen(wf, wc, wd, liczbaTematow);
                    if (ocena > najlepszyWynik) {
                        najlepszyWynik = ocena;
                        najlepsza = p;
                    }
                    p.ksiazki.remove(k);
                }
            }

            if (najlepsza != null) najlepsza.dodaj(k);
        }

        return najlepszy;
    }
}
