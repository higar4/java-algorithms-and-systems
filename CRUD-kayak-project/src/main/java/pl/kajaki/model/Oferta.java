package pl.kajaki.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Oferta implements Serializable {
    private String id;
    private String miejsce;
    private LocalDate data;
    private int maxMiejsc;
    private int zajeteMiejsca;

    public Oferta(String id, String miejsce, String dataStr, int maxMiejsc) {
        this.id = id;
        this.miejsce = miejsce;
        this.data = LocalDate.parse(dataStr);
        this.maxMiejsc = maxMiejsc;
        this.zajeteMiejsca = 0;
    }

    public String getId() { return id; }
    public int getWolneMiejsca() { return maxMiejsc - zajeteMiejsca; }
    public void zarezerwuj(int ile) { this.zajeteMiejsca += ile; }
    public LocalDate getData() { return data; }

    @Override
    public String toString() {
        return "Oferta " + id + ": " + miejsce + " (" + data + ") - Wolnych: " + getWolneMiejsca();
    }
}