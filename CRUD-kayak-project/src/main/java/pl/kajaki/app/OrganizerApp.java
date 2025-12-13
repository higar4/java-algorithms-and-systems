package pl.kajaki.app;

import pl.kajaki.model.Rezerwacja;
import pl.kajaki.db.BazaDanych;
import pl.kajaki.model.Oferta;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class OrganizerApp {
    public static void main(String[] args) {
        BazaDanych baza = new BazaDanych();
        Scanner skaner = new Scanner(System.in);
        System.out.println("--- PANEL ORGANIZATORA ---");
        while (true) {
            System.out.println("\n1. Wyświetl wszystkie oferty");
            System.out.println("2. Dodaj nową ofertę spływu");
            System.out.println("3. Dodaj gotowe oferty (szybki test)");
            System.out.println("4. Zarządzaj spływem (Potwierdź/Odwołaj)");
            System.out.println("5. Wyjdź");
            System.out.print("Twój wybór: ");
            String wybor = skaner.nextLine();
            try {
                if (wybor.equals("1")) {
                    List<Oferta> oferty = baza.pobierzOferty();
                    if (oferty.isEmpty()) System.out.println("Brak ofert");
                    for (Oferta o : oferty) {
                        System.out.println(o);
                    }
                }
                else if (wybor.equals("2")) {
                    System.out.print("Podaj lokalizację: ");
                    String miejsce = skaner.nextLine();
                    System.out.print("Podaj datę (format Rok-Miesiąc-Dzień): ");
                    String data = skaner.nextLine();
                    System.out.print("Maksymalna liczba kajaków: ");
                    int max = Integer.parseInt(skaner.nextLine());
                    String id = UUID.randomUUID().toString().substring(0, 4);
                    List<Oferta> aktualneOferty = baza.pobierzOferty();
                    Oferta nowa = new Oferta(id, miejsce, data, max);
                    aktualneOferty.add(nowa);
                    baza.zapiszOferty(aktualneOferty);
                    System.out.println("Dodano nową ofertę");
                }
                else if (wybor.equals("3")) {
                    System.out.println("Organizator: Tworzę przykładowe oferty...");
                    List<Oferta> aktualneOferty = baza.pobierzOferty();
                    String id1 = UUID.randomUUID().toString().substring(0, 4);
                    String id2 = UUID.randomUUID().toString().substring(0, 4);
                    aktualneOferty.add(new Oferta(id1, "Dunajec (Test)", "2026-06-01", 10));
                    aktualneOferty.add(new Oferta(id2, "Czarna Hańcza (Test)", "2026-07-15", 5));
                    baza.zapiszOferty(aktualneOferty);
                    System.out.println("Dodano 2 oferty testowe");
                }
                else if (wybor.equals("4")) {
                    System.out.print("Podaj ID oferty do weryfikacji: ");
                    String idOferty = skaner.nextLine();
                    List<Rezerwacja> rezerwacje = baza.pobierzRezerwacje();
                    int licznik = 0;
                    for (Rezerwacja r : rezerwacje) {
                        if (r.getIdOferty().equals(idOferty) && r.getStatus() == pl.kajaki.model.StatusRezerwacji.ZALOZONA) {
                            licznik++;
                        }
                    }
                    System.out.println("Znaleziono " + licznik + " niepotwierdzonych rezerwacji na ten spływ");
                    System.out.println("Co chcesz zrobić?");
                    System.out.println("T - Zatwierdź spływ (status -> POTWIERDZONA)");
                    System.out.println("N - Odwołaj spływ (status -> ODWOLANA)");
                    String decyzja = skaner.nextLine().toUpperCase();
                    boolean zmiany = false;
                    for (Rezerwacja r : rezerwacje) {
                        if (r.getIdOferty().equals(idOferty) && r.getStatus() == pl.kajaki.model.StatusRezerwacji.ZALOZONA) {
                            if (decyzja.equals("T")) {
                                r.setStatus(pl.kajaki.model.StatusRezerwacji.POTWIERDZONA);
                                zmiany = true;
                            } else if (decyzja.equals("N")) {
                                r.setStatus(pl.kajaki.model.StatusRezerwacji.ODWOLANA);
                                zmiany = true;
                            }
                        }
                    }
                    if (zmiany) {
                        baza.zapiszRezerwacje(rezerwacje);
                        System.out.println("Statusy rezerwacji zostały zaktualizowane");
                    } else {
                        System.out.println("Brak zmian");
                    }
                }
                else if (wybor.equals("5")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
            }
        }
    }
}