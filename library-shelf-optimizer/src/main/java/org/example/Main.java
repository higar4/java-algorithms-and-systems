    package org.example;

    import java.io.*;
    import java.util.*;

    public class Main {
        public static void main(String[] args) throws IOException {
            Scanner sc = new Scanner(System.in);

            System.out.print("Podaj nazwę pliku z półkami (np. regal.txt): ");
            String plikRegal = sc.nextLine();

            System.out.print("Podaj nazwę pliku z książkami (np. ksiazki.txt): ");
            String plikKsiazki = sc.nextLine();

            System.out.print("Podaj wagę wf: ");
            double wf = sc.nextDouble();

            System.out.print("Podaj wagę wc: ");
            double wc = sc.nextDouble();

            System.out.print("Podaj wagę wd: ");
            double wd = sc.nextDouble();

            List<Polka> polki = wczytajPolki(plikRegal);
            List<Ksiazka> ksiazki = wczytajKsiazki(plikKsiazki);

            Regal najlepszy = Optymalizator.optymalizuj(polki, ksiazki, wf, wc, wd);

            System.out.println("\nNajlepsze rozmieszczenie:");
            System.out.println(najlepszy);
            System.out.println("Ocena: " + najlepszy.ocen(wf, wc, wd,
                    (int) ksiazki.stream().map(k -> k.temat).distinct().count()));
        }

        static List<Polka> wczytajPolki(String plik) throws IOException {
            List<Polka> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#") || line.isBlank()) continue;
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0].trim());
                    double h = Double.parseDouble(parts[1].trim());
                    double w = Double.parseDouble(parts[2].trim());
                    list.add(new Polka(id, h, w));
                }
            }
            return list;
        }

        static List<Ksiazka> wczytajKsiazki(String plik) throws IOException {
            List<Ksiazka> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#") || line.isBlank()) continue;
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0].trim());
                    double h = Double.parseDouble(parts[1].trim());
                    double g = Double.parseDouble(parts[2].trim());
                    int t = Integer.parseInt(parts[3].trim());
                    list.add(new Ksiazka(id, h, g, t));
                }
            }
            return list;
        }
    }
