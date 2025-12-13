# 📚 Library Shelf Optimizer

Projekt algorytmiczny symulujący inteligentne zarządzanie przestrzenią w bibliotece. Aplikacja rozwiązuje problem optymalnego rozmieszczenia książek na regałach (wariacja problemu upakowania - *Bin Packing Problem*), uwzględniając fizyczne ograniczenia półek.

![Java](https://img.shields.io/badge/Java-SE-orange) ![Algorithms](https://img.shields.io/badge/Algorithms-Optimization-red) ![OOP](https://img.shields.io/badge/Pattern-OOP-blue)

## 💡 O Projekcie

Celem projektu jest symulacja procesu układania książek na regałach w taki sposób, aby maksymalnie wykorzystać dostępną przestrzeń. System modeluje fizyczne obiekty (Książka, Półka, Regał) i wykorzystuje algorytm w klasie `Optymalizator` do automatycznej organizacji zbioru.

### Kluczowe funkcjonalności:
* **Algorytm Upakowania:** Logika decydująca, czy książka zmieści się na danej półce, czy należy otworzyć nową (Next-Fit / First-Fit strategy).
* **Modelowanie Obiektowe:** Hierarchiczna struktura danych: `Regał` -> zawiera `Półki` -> zawierają `Książki`.
* **Import Danych:** Możliwość wczytania listy książek z pliku tekstowego (np. `ksiazki.txt`).

## 🏗️ Struktura Klas (Logika Biznesowa)

Projekt oparty jest na ścisłych relacjach między obiektami:

| Klasa | Odpowiedzialność |
| :--- | :--- |
| **`Optymalizator`** | **Mózg systemu.** Przyjmuje listę książek i pusty regał, a następnie decyduje o rozmieszczeniu tomów zgodnie z algorytmem. |
| **`Regal`** | Kontener na półki. Zarządza całkowitą pojemnością biblioteczki. |
| **`Polka`** | Posiada ograniczoną szerokość (np. 50 cm). Pilnuje, aby suma grubości książek nie przekroczyła limitu. |
| **`Ksiazka`** | Model danych zawierający tytuł, autora oraz wymiary (grubość), które są kluczowe dla algorytmu. |

## ⚙️ Jak działa algorytm?

Aplikacja implementuje podejście zachłanne (Greedy Algorithm):
1.  Pobierz pierwszą książkę z listy.
2.  Sprawdź, czy zmieści się na bieżącej półce (suma grubości < szerokość półki).
3.  **TAK:** Dodaj książkę do półki.
4.  **NIE:** Zamknij obecną półkę, utwórz nową w regale i tam wstaw książkę.
5.  Powtarzaj do wyczerpania listy książek.
