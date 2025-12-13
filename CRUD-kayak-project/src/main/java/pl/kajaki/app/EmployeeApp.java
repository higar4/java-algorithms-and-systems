package pl.kajaki.app;

import pl.kajaki.db.BazaDanych;
import pl.kajaki.model.Oferta;
import pl.kajaki.model.Rezerwacja;
import pl.kajaki.model.StatusRezerwacji;
import java.util.List;
import java.util.Scanner;

public class EmployeeApp {
    public static void main(String[] args) {
        BazaDanych baza = new BazaDanych();
        Scanner skaner = new Scanner(System.in);
        System.out.println("--- PANEL PRACOWNIKA ---");
        while (true) {
            System.out.println("\n1. Pokaż rezerwacje do obsłużenia");
            System.out.println("2. Zmień status rezerwacji");
            System.out.println("3. Pokaż wszystkie oferty");
            System.out.println("4. Usuń ofertę");
            System.out.println("5. Wyjdź");
            System.out.print("Wybór: ");
            String opcja = skaner.nextLine();
            try {
                if (opcja.equals("1")) {
                    List<Rezerwacja> rezerwacje = baza.pobierzRezerwacje();
                    if (rezerwacje.isEmpty()) System.out.println("Brak rezerwacji");
                    for (Rezerwacja r : rezerwacje) {
                        System.out.println(r);
                    }
                }
                else if (opcja.equals("2")) {
                    System.out.print("Podaj ID rezerwacji: ");
                    String id = skaner.nextLine();
                    List<Rezerwacja> rezerwacje = baza.pobierzRezerwacje();
                    boolean znaleziono = false;
                    for (Rezerwacja r : rezerwacje) {
                        if (r.getId().equals(id)) {
                            r.setStatus(StatusRezerwacji.ZREALIZOWANA);
                            znaleziono = true;
                            break;
                        }
                    }
                    if (znaleziono) {
                        baza.zapiszRezerwacje(rezerwacje);
                        System.out.println("Status zmieniony na ZREALIZOWANA");
                    } else {
                        System.out.println("Nie znaleziono takiej rezerwacji");
                    }
                }
                else if (opcja.equals("3")) {
                    List<Oferta> oferty = baza.pobierzOferty();
                    if (oferty.isEmpty()) System.out.println("Brak ofert");
                    for(Oferta o : oferty) System.out.println(o);
                }
                else if (opcja.equals("4")) {
                    System.out.print("Podaj ID oferty do usunięcia: ");
                    String idDoUsuniecia = skaner.nextLine();
                    List<Oferta> oferty = baza.pobierzOferty();
                    boolean usunieto = oferty.removeIf(oferta -> oferta.getId().equals(idDoUsuniecia));
                    if (usunieto) {
                        baza.zapiszOferty(oferty);
                        System.out.println("Oferta została usunięta");
                    } else {
                        System.out.println("BŁĄD: Nie znaleziono oferty o takim ID");
                    }
                }
                else if (opcja.equals("5")) break;

            } catch (Exception e) {
                System.out.println("Błąd systemu: " + e.getMessage());
            }
        }
    }
}