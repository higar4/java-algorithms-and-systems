package pl.kajaki.db;

import pl.kajaki.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BazaDanych {
    private final String nazwaPliku = "baza_kajaki.dat";

    public List<Oferta> pobierzOferty() {
        File file = new File(nazwaPliku);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Oferta>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Błąd odczytu bazy (może inna aplikacja korzysta): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void zapiszOferty(List<Oferta> oferty) throws Exception {
        int proby = 0;
        while (proby < 5) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nazwaPliku))) {
                oos.writeObject(oferty);
                return;
            } catch (IOException e) {
                proby++;
                System.out.println("Plik zablokowany. Czekam... (Próba " + proby + ")");
                Thread.sleep(500);
            }
        }
        throw new Exception("Nie udało się zapisać danych - system zajęty");
    }

    private final String plikRezerwacji = "baza_rezerwacje.dat";

    public List<Rezerwacja> pobierzRezerwacje() {
        File file = new File(plikRezerwacji);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Rezerwacja>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void zapiszRezerwacje(List<Rezerwacja> rezerwacje) throws Exception {
        int proby = 0;
        while (proby < 5) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(plikRezerwacji))) {
                oos.writeObject(rezerwacje);
                return;
            } catch (IOException e) {
                proby++;
                Thread.sleep(100);
            }
        }
        throw new KajakiException("Błąd zapisu rezerwacji - plik zablokowany");
    }
}

