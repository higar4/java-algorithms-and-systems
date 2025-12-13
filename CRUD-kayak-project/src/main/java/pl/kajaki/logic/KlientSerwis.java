package pl.kajaki.logic;

import pl.kajaki.db.BazaDanych;
import pl.kajaki.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KlientSerwis {
    private BazaDanych baza = new BazaDanych();

    public String wyswietlOferty() {
        List<Oferta> oferty = baza.pobierzOferty();
        if (oferty.isEmpty()) return "Brak ofert w systemie";
        StringBuilder sb = new StringBuilder();
        for (Oferta o : oferty) {
            sb.append(o.toString()).append("\n");
        }
        return sb.toString();
    }

    public void zrobRezerwacje(String idOferty, int iloscOsob, String nazwaKlienta) throws Exception {
        List<Oferta> oferty = baza.pobierzOferty();
        List<Rezerwacja> rezerwacje = baza.pobierzRezerwacje();
        Oferta wybrana = null;
        for (Oferta o : oferty) {
            if (o.getId().equals(idOferty)) {
                wybrana = o;
                break;
            }
        }
        if (wybrana == null) throw new Exception("Nie znaleziono oferty");
        if (LocalDate.now().plusDays(1).isAfter(wybrana.getData())) {
            throw new KajakiException("Za późno Rezerwację trzeba składać min. 1 dzień przed spływem");
        }
        if (wybrana.getWolneMiejsca() < iloscOsob) {
            throw new KajakiException("Za mało wolnych kajaków");
        }
        String idRez = UUID.randomUUID().toString().substring(0, 8);
        Rezerwacja nowa = new Rezerwacja(idRez, nazwaKlienta, idOferty, iloscOsob);
        wybrana.zarezerwuj(iloscOsob);
        rezerwacje.add(nowa);
        baza.zapiszOferty(oferty);
        baza.zapiszRezerwacje(rezerwacje);
    }
    public String pobierzRezerwacjeKlienta(String nazwaKlienta) {
        List<Rezerwacja> wszystkie = baza.pobierzRezerwacje();
        StringBuilder sb = new StringBuilder();
        boolean znaleziono = false;

        for (Rezerwacja r : wszystkie) {
            if (r.getIdKlienta().equalsIgnoreCase(nazwaKlienta)) {
                sb.append(r.toString()).append("\n");
                znaleziono = true;
            }
        }
        if (!znaleziono) return "Brak rezerwacji dla klienta: " + nazwaKlienta;
        return sb.toString();
    }
}