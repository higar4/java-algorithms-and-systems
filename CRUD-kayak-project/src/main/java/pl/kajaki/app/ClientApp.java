package pl.kajaki.app;

import pl.kajaki.logic.KlientSerwis;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        KlientSerwis serwis = new KlientSerwis();
        Scanner skaner = new Scanner(System.in);
        System.out.print("Podaj swoje imię/nazwę klienta: ");
        String imieKlienta = skaner.nextLine();
        System.out.println("Witaj " + imieKlienta);

        while (true) {
            System.out.println("\n--- MENU KLIENTA ---");
            System.out.println("1. Pokaż dostępne spływy");
            System.out.println("2. Zrób nową rezerwację");
            System.out.println("3. Moje rezerwacje");
            System.out.println("4. Wyjdź");
            System.out.print("Wybór: ");
            String wybor = skaner.nextLine();
            try {
                if (wybor.equals("1")) {
                    System.out.println(serwis.wyswietlOferty());
                }
                else if (wybor.equals("2")) {
                    System.out.print("Podaj ID spływu: ");
                    String idOferty = skaner.nextLine();
                    System.out.print("Ile kajaków: ");
                    int ile = Integer.parseInt(skaner.nextLine());
                    serwis.zrobRezerwacje(idOferty, ile, imieKlienta);
                    System.out.println("Zarezerwowano!");
                }
                else if (wybor.equals("3")) {
                    System.out.println("--- HISTORIA REZERWACJI ---");
                    System.out.println(serwis.pobierzRezerwacjeKlienta(imieKlienta));
                }
                else if (wybor.equals("4")) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("BŁĄD: " + e.getMessage());
            }
        }
    }
}