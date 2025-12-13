# 📈 GUS BDL Data Viewer

Aplikacja desktopowa w Javie służąca do przeglądania danych statystycznych z **Banku Danych Lokalnych (BDL)** Głównego Urzędu Statystycznego.

Projekt demonstracyjny pokazujący wykorzystanie **Java Platform Module System (JPMS)**, klienta HTTP wbudowanego w JDK 11+ oraz architektury wielowarstwowej.

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue) ![Swing](https://img.shields.io/badge/GUI-Swing-green) ![License](https://img.shields.io/badge/License-MIT-lightgrey)


## 🚀 Funkcjonalności

* **Pobieranie danych na żywo:** Aplikacja łączy się z publicznym API GUS (https://api.stat.gov.pl/).
* **Analiza JSON:** Parsowanie złożonych odpowiedzi API przy użyciu biblioteki `org.json`.
* **Wielowątkowość:** Operacje sieciowe wykonywane są w tle, nie blokując interfejsu użytkownika (Swing).
* **Architektura Modułowa:** Projekt podzielony na logiczne moduły zgodnie z Java 9+ Modules:
    * `lab04_client` - logika biznesowa i komunikacja HTTP.
    * `lab04_gui` - warstwa prezentacji (Swing).

## 🛠️ Technologie

Projekt wykorzystuje następujący stos technologiczny:
* **Java 17** - Core language.
* **Java HttpClient** (`java.net.http`) - Nowoczesny, asynchroniczny klient HTTP.
* **Swing** - Biblioteka do budowy interfejsu graficznego.
* **Maven** - Zarządzanie zależnościami i cyklem życia projektu (Multi-module project).
* **org.json** - Biblioteka do przetwarzania formatu JSON.

## 📂 Struktura Projektu

```text
lab04/
├── lab04_client/       # Moduł odpowiedzialny za logikę (Backend)
│   ├── GusClient.java  # Obsługa HttpClient
│   └── GusData.java    # Rekord danych (DTO)
├── lab04_gui/          # Moduł odpowiedzialny za widok (Frontend)
│   └── MainWindow.java # Interfejs Swing i tabela danych
└── pom.xml             # Główny plik konfiguracyjny Maven