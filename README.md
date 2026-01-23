# 🚀 Java Algorithms & Systems Portfolio

Witaj w moim repozytorium! Znajdziesz tutaj zbiór projektów zrealizowanych w ramach studiów, koncentrujących się na **Javie**, **algorytmice** oraz **inżynierii oprogramowania**.

Każdy projekt porusza inne aspekty programowania: od integracji z zewnętrznymi API i tworzenia GUI, przez systemy współbieżne i rozproszone, aż po optymalizację algorytmiczną.

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Maven](https://img.shields.io/badge/Tools-Maven-red) ![Git](https://img.shields.io/badge/Tools-Git-f05032) ![Status](https://img.shields.io/badge/Status-Active-brightgreen)

## 📂 Przegląd Projektów

| Projekt                    | Opis | Kluczowe Technologie | Link |
|:---------------------------| :--- | :--- | :---: |
| **🚛 Sewage System Simulator** | System rozproszony w architekturze Klient-Serwer symulujący proces zamawiania i wywozu nieczystości. Składa się z 4 niezależnych aplikacji komunikujących się własnym protokołem tekstowym przez TCP/IP. Każdy moduł posiada panel sterowania GUI. | `Java Sockets` `TCP/IP` `Distributed Systems` `Swing` | [Zobacz](./Sewage-System) |
| **🏭 Production Line** | Symulator systemów współbieżnych wizualizujący problem producenta-konsumenta na wspólnym torze. Implementuje autorskie algorytmy zapobiegania zakleszczeniom (Deadlock Prevention) i priorytetyzacji wątków. | `Java Threads` `Swing` `Monitor Pattern` `Traffic Algorithms` | [Zobacz](./lab05-production-line) |
| **📚 Library Optimizer** | Projekt algorytmiczny rozwiązujący problem optymalnego rozmieszczenia książek na półkach (Bin Packing Problem). | `Algorithms` `Greedy Strategy` `OOP` `Optimization` | [Zobacz](./library-shelf-optimizer) |
| **🛶 Kayak Rental CRUD** | Rozproszony system rezerwacji kajaków symulujący pracę na współdzielonych zasobach. Obsługuje wielodostęp i transakcyjność na plikach. | `Concurrency` `Java IO` `Serialization` `State Machine` | [Zobacz](./CRUD-kayak-project) |
| **📊 GUS BDL Data Viewer** | Aplikacja okienkowa (GUI) pobierająca dynamicznie dane statystyczne z Banku Danych Lokalnych (GUS). Prezentuje wskaźniki makroekonomiczne dla województw. | `REST API` `JSON` `Swing/AWT` `Http Client` | [Zobacz](./GUS-API) |

## 🛠️ Stack Technologiczny

W projektach wykorzystuję sprawdzone wzorce i narzędzia:
* **Core:** Java SE (Collections, Stream API, Multithreading)
* **Network:** Java Sockets (TCP/IP), Custom Protocols, Client-Server Architecture
* **Build:** Maven
* **Data:** JSON (org.json), Java Serialization, Custom File Parsing
* **VCS:** Git & GitHub

---
*Repozytorium utrzymywane przez [Piotr Francuz](https://github.com/higar4)*