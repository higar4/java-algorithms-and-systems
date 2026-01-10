# 🏭 Production Line Simulator (Concurrent System)

Zaawansowany symulator stacji przeładunkowej, demonstrujący mechanizmy synchronizacji wątków w środowisku z ograniczonymi zasobami przestrzennymi. Projekt rozwiązuje klasyczne problemy współbieżności, takie jak **zakleszczenia (Deadlocks)**, **zagłodzenie (Starvation)** oraz **wyścigi (Race Conditions)**.

![Java](https://img.shields.io/badge/Java-Concurrency-red) ![Swing](https://img.shields.io/badge/GUI-Swing-blue) ![Pattern](https://img.shields.io/badge/Pattern-Monitor-green) ![Algorithm](https://img.shields.io/badge/Algo-Deadlock%20Prevention-orange)

## 💡 O Projekcie

System symuluje pracę stacji składającej się z rampy wejściowej, rampy wyjściowej oraz jednowymiarowego toru, po którym poruszają się autonomiczne podajniki (Loaders).
Wyzwaniem projektu jest koordynacja ruchu podajników, które nie mogą się wyprzedzać i muszą współdzielić wąskie gardło (tor), unikając blokad.

### Kluczowe wyzwania inżynieryjne:
* **Spatial Resource Constraint:** Podajniki fizycznie blokują sobie drogę.
* **Starvation Avoidance:** Implementacja mechanizmu "starzenia się" (Aging/Urgency), aby zapobiec dominacji jednego wątku.
* **Livelock & Deadlock Prevention:** Autorski protokół rozwiązywania konfliktów oparty na priorytetach i "uprzejmym ustępowaniu" (Courtesy Yielding).

## 🏗️ Architektura Systemu

Aplikacja oparta jest na wzorcu **MVC (Model-View-Controller)** oraz **Monitorze**:

| Komponent                     | Rola |
|:------------------------------| :--- |
| **`Station` (Monitor)**       | Centralny punkt synchronizacji. Wykorzystuje metody `synchronized`, `wait()` i `notifyAll()` do zarządzania stanem planszy i zapewnienia atomowości operacji. |
| **`Loader` (Thread)**         | "Mózg" podajnika. Implementuje logikę ruchu, wykrywanie kolizji oraz negocjacje pierwszeństwa. |
| **`Creator` / `Annihilator`** | Wątki producenta i konsumenta, symulujące otoczenie zewnętrzne stacji. |
| **`StationPanel` (Gui)**      | Warstwa wizualizacji oparta na `Swing` i `Graphics2D` (renderowanie 60 FPS). |

## 🚦 Algorytm Anty-Zakleszczeniowy (Smart Traffic Protocol)

Aby podajniki nie blokowały się nawzajem, zaimplementowano trzystopniowy protokół negocjacji:

1.  **Reguła Ładunku:** Podajnik niosący towar ma bezwzględne pierwszeństwo przed pustym.
2.  **Mechanizm Pilności (Urgency/Aging):**
    * Gdy dwa podajniki z towarem spotkają się naprzeciw siebie, wygrywa ten, który **dłużej trzyma ładunek**.
    * Zapobiega to sytuacji, w której "świeży" podajnik blokuje tego, który czeka od dawna.
3.  **Dominance Patience (Inteligentne Ustępowanie):**
    * Jeśli podajnik ma pierwszeństwo, ale widzi, że rywal jest zablokowany przez ścianę, **tymczasowo rezygnuje ze swojego prawa** i cofa się, aby udrożnić zator.

## 🚀 Uruchomienie

1.  Wymagania: Java 17+
2.  Uruchom klasę główną: `org.production.Main`
3.  Parametry symulacji (X, Y, K) można edytować w pliku `Main.java`.

## 📸 Wizualizacja

GUI prezentuje stan w czasie rzeczywistym:
* 🟦 **Niebieska strefa:** Rampa wejściowa (pojawiają się towary).
* ⬜ **Szary tor:** Strefa ruchu podajników.
* 🟩 **Zielona strefa:** Rampa wyjściowa (towary znikają).
* 🟧 **Podajniki:** Ciemniejsze = zajęte, Jaśniejsze = puste.
