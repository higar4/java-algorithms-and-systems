# Symulator Systemu Wywozu Nieczystości (Sewage System)

Projekt realizujący system rozproszony oparty na gniazdach TCP/IP (Java Sockets). Aplikacja symuluje proces zamawiania, wywozu i utylizacji nieczystości płynnych w modelu **Klient-Serwer** z wykorzystaniem wielowątkowości.

Każdy komponent systemu posiada własny Graficzny Interfejs Użytkownika (GUI) oparty na bibliotece Swing, umożliwiający ręczne sterowanie procesami.

## 🏗️ Architektura Systemu

System składa się z 4 niezależnych aplikacji (klas z metodą `main`), które komunikują się ze sobą tekstowym protokołem TCP:

1.  **House (Dom)** - Port `6000`
    * Produkuje nieczystości.
    * Zamawia wywóz w Biurze (`order`).
    * Udostępnia interfejs do wypompowania szamba (`getPumpOut`).
2.  **Office (Biuro)** - Port `8000`
    * Przyjmuje zlecenia od Domów (`order`).
    * Rejestruje Cysterny (`register`).
    * Zleca Cysternom zadania (`setJob`).
    * Rozlicza się z Oczyszczalnią (`setPayOff`).
3.  **Tanker (Cysterna)** - Port `7000`
    * Realizuje zlecenia: jedzie do Domu, pobiera ścieki, wiezie do Oczyszczalni.
    * Zgłasza gotowość w Biurze (`setReadyToServe`).
4.  **SewagePlant (Oczyszczalnia)** - Port `9001`
    * Przyjmuje zrzuty ścieków (`setPumpIn`).
    * Udostępnia stan napełnienia dla Biura (`getStatus`).

## 🔌 Protokół Komunikacyjny

Komunikacja odbywa się za pomocą wiadomości tekstowych zakończonych znakiem nowej linii.

| Aktor A | Aktor B | Komenda (Request) | Odpowiedź (Response) | Opis |
| :--- | :--- | :--- | :--- | :--- |
| Tanker | Office | `r:port,host` | `ID` (int) | Rejestracja cysterny |
| House | Office | `o:port,host` | `1` (OK) / `0` (Err) | Zamówienie wywozu |
| Office | Tanker | `sj:port,host` | `1` | Zlecenie zadania (adres Domu) |
| Tanker | Office | `sr:id` | `1` | Zgłoszenie gotowości |
| Tanker | House | `gp:max_vol` | `amount` (int) | Pobranie ścieków (ile pobrano) |
| Tanker | Plant | `spi:id,vol` | `1` | Zrzut ścieków |
| Office | Plant | `gs:id` | `amount` (int) | Sprawdzenie stanu (do zapłaty) |
| Office | Plant | `spo:id` | `1` | Opłacenie i wyzerowanie stanu |

## 🚀 Instrukcja Uruchomienia (Scenariusz "Control Room")

Aplikacje należy uruchamiać w osobnych procesach (terminalach lub konfiguracjach Run w IDE) w następującej kolejności, aby uniknąć błędów połączenia (`ConnectionRefused`):

1.  **Uruchom `SewagePlant`** (Oczyszczalnia).
2.  **Uruchom `Office`** (Biuro).
3.  **Uruchom `Tanker`** (Cysterna).
4.  **Uruchom `House`** (Dom).

### Przykładowy scenariusz użycia (Manualny):

1.  **Konfiguracja:**
    * W oknie **Tanker** kliknij `1. Rejestracja`. Otrzymasz ID.
    * W oknie **Tanker** kliknij `2. Zgłoś Gotowość`.
2.  **Produkcja:**
    * W oknie **House** klikaj `Produkuj Ścieki`, aż pasek zapełni się (zmieni kolor na czerwony).
    * Kliknij `Zamów Wywóz`.
3.  **Dyspozycja:**
    * W oknie **Office** zobaczysz na listach: *Wolną Cysternę* oraz *Oczekujące Zlecenie*.
    * Zaznacz obie pozycje na listach i kliknij `PRZYDZIEL ZLECENIE`.
4.  **Realizacja:**
    * W oknie **Tanker** pojawi się status zlecenia. Kliknij `3. Pompuj`. Pasek w domu spadnie, w cysternie wzrośnie.
    * W oknie **Tanker** kliknij `4. Zrzut`.
5.  **Rozliczenie:**
    * W oknie **SewagePlant** zobaczysz przyjęte ścieki w tabeli.
    * W oknie **Tanker** kliknij `2. Zgłoś Gotowość` (aby wrócić do puli wolnych pojazdów).
    * **Office** automatycznie wykryje zrzut, opłaci go (logi w Biurze: `$$$ Opłacono`) i wyzeruje stan w Oczyszczalni.
