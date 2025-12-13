package pl.kajaki.model;

import java.io.Serializable;

public class Rezerwacja implements Serializable {
    private String id;
    private String idKlienta;
    private String idOferty;
    private int liczbaMiejsc;
    private StatusRezerwacji status;

    public Rezerwacja(String id, String idKlienta, String idOferty, int liczbaMiejsc) {
        this.id = id;
        this.idKlienta = idKlienta;
        this.idOferty = idOferty;
        this.liczbaMiejsc = liczbaMiejsc;
        this.status = StatusRezerwacji.ZALOZONA;
    }

    public String getId() { return id; }
    public String getIdKlienta() {return idKlienta;}
    public String getIdOferty() { return idOferty; }
    public int getLiczbaMiejsc() { return liczbaMiejsc; }
    public StatusRezerwacji getStatus() { return status; }
    public void setStatus(StatusRezerwacji status) { this.status = status; }

    @Override
    public String toString() {
        return "Rezerwacja [" + id + "] Klient: " + idKlienta + ", Miejsc: " + liczbaMiejsc + ", Status: " + status;
    }
}