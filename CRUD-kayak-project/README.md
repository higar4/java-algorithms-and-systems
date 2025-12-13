# 🛶 CRUD Kayak Project (System Rezerwacji)

Symulacja rozproszonego systemu obsługi wypożyczalni kajaków, opartego na współdzieleniu zasobów plikowych. Projekt demonstruje implementację operacji **CRUD** oraz obsługę współbieżności bez użycia relacyjnej bazy danych.

![Java](https://img.shields.io/badge/Java-SE-orange) ![IO](https://img.shields.io/badge/Java-IO-blue) ![CRUD](https://img.shields.io/badge/Pattern-CRUD-green) ![Serialization](https://img.shields.io/badge/Data-Serialization-purple)

## 💡 O Projekcie

System składa się z trzech niezależnych aplikacji konsolowych, które operują na wspólnym stanie zapisanym w plikach `.dat`. Aplikacja symuluje środowisko produkcyjne, w którym wielu użytkowników (Klientów, Pracowników, Organizatorów) próbuje jednocześnie modyfikować te same dane.

### Kluczowe wyzwania techniczne:
* **Custom Persistence Layer:** Własna implementacja zapisu obiektów (Java Serialization).
* **Concurrency Control:** Mechanizm bezpiecznego dostępu do plików (retry policy) zapobiegający uszkodzeniu danych przy równoczesnym zapisie.
* **Business Logic:** Obsługa pełnego cyklu życia rezerwacji (State Machine).

## 🏗️ Moduły Systemu

Projekt podzielony jest na trzy role (aplikacje), realizujące różne aspekty CRUD:

### 1. OrganizerApp (Administrator)
* **Create:** Dodawanie nowych kajaków i wycieczek do oferty.
* **Update:** Zatwierdzanie rezerwacji klientów (zmiana statusu na `POTWIERDZONA`).
* **Read:** Przegląd wszystkich rezerwacji i raportów.

### 2. ClientApp (Użytkownik)
* **Read:** Przeglądanie dostępnych ofert wycieczek.
* **Create:** Składanie nowej rezerwacji (status `ZALOZONA`).
* **Delete:** Anulowanie własnej rezerwacji przed zatwierdzeniem.

### 3. EmployeeApp (Pracownik Terenowy)
* **Read:** Wyszukiwanie rezerwacji po ID lub nazwisku.
* **Update:** Wydanie sprzętu i oznaczenie wycieczki jako `ZREALIZOWANA`.

## ⚙️ Workflow (Scenariusz użycia)

Aby przetestować pełny przepływ danych, uruchom aplikacje w następującej kolejności:

1.  🔴 **Uruchom `OrganizerApp`**
    * Wybierz opcję *Generuj ofertę* (lub dodaj ręcznie).
    * *Cel: Utworzenie pliku bazy danych.*
2.  🟢 **Uruchom `ClientApp`**
    * Zaloguj się jako np. "Jan Kowalski".
    * Wybierz wycieczkę i zarezerwuj miejsce.
3.  🔴 **Wróć do `OrganizerApp`**
    * Znajdź rezerwację Jana.
    * Zatwierdź ją (Status zmienia się na `POTWIERDZONA`).
4.  🔵 **Uruchom `EmployeeApp`**
    * Wyszukaj rezerwację klienta.
    * Zatwierdź wydanie kajaka (Status zmienia się na `ZREALIZOWANA`).

## 🛠️ Warstwa Techniczna

### Struktura Danych
Dane są trwale zapisywane w katalogu roboczym w formacie binarnym:
* `baza_oferty.dat` – Katalog wycieczek i sprzętu.
* `baza_rezerwacje.dat` – Rejestr operacji klientów.

### Obsługa Błędów IO (Retries)
W klasie dostępu do danych zaimplementowano mechanizm ponawiania prób w przypadku blokady pliku przez inny proces:

```java
// Pseudokod logiki zapisu
public void zapiszZbiór(Object dane) {
    int proby = 0;
    while (proby < 3) {
        try {
            // Próba otwarcia strumienia i zapisu...
            objectOutputStream.writeObject(dane);
            return; // Sukces
        } catch (IOException e) {
            proby++;
            Thread.sleep(1000); // Czekaj na zwolnienie zasobu
        }
    }
    throw new SystemBusyException("Baza danych jest zajęta.");
}