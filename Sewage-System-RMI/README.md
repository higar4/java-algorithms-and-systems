# 🔗 Sewage System RMI (Distributed Objects)

Alternatywna implementacja systemu wywozu nieczystości, w której niskopoziomowa komunikacja gniazdami (Sockets) została zastąpiona technologią **Java RMI (Remote Method Invocation)**.

Projekt demonstruje, jak w przezroczysty dla użytkownika sposób wywoływać metody na obiektach znajdujących się na zdalnych maszynach wirtualnych (JVM), eliminując konieczność ręcznego parsowania protokołów tekstowych.

![Java](https://img.shields.io/badge/Java-21%2B-orange) ![RMI](https://img.shields.io/badge/Tech-Java%20RMI-red) ![Swing](https://img.shields.io/badge/GUI-Swing-green) ![Architecture](https://img.shields.io/badge/Arch-Distributed-blue)

## 💡 O Projekcie (RMI vs Sockets)

Podczas gdy wersja "Socketowa" opierała się na przesyłaniu ciągów znaków (np. `gp:200`), wersja RMI operuje na **interfejsach zdalnych**.

### Kluczowe różnice technologiczne:
* **Komunikacja:** Zamiast `PrintWriter` i `BufferedReader`, używamy metod interfejsów (np. `house.getPumpOut(200)`).
* **Adresowanie:** Zamiast adresów IP i portów, przekazujemy **referencje do obiektów** (Stubs).
* **Rejestr (Krawiec/Tailor):** Wprowadzono specjalny moduł "zszywający" system, który pełni rolę niestandardowego rejestru RMI (`Naming Service`).

## 🏗️ Architektura Systemu

System składa się z 5 modułów, które muszą zostać uruchomione w osobnych procesach:

| Moduł | Rola w systemie RMI |
| :--- | :--- |
| **`Tailor`** (Krawiec) | **Niestandardowy Rejestr.** Uruchamia rejestr RMI. Pozostałe moduły zgłaszają się do niego, aby pobrać "namiastki" (referencje) do innych podsystemów. |
| **`SewagePlant`** | Udostępnia zdalnie metody do zrzutu ścieków (`setPumpIn`) i sprawdzania stanu płatności. Rejestruje się u Krawca. |
| **`Office`** | "Mózg" operacji. Pobiera od Krawca referencję do Oczyszczalni. Udostępnia metody do rejestracji Cystern i składania zamówień przez Domy. |
| **`Tanker`** | Pobiera referencję do Biura i Oczyszczalni. Otrzymuje od Biura referencję do konkretnego obiektu `House` do obsłużenia. |
| **`House`** | Klient. Pobiera referencję do Biura i przekazuje **siebie (`this`)** jako parametr metody `order()`, aby Cysterna mogła do niego "przyjechać" (wywołać zwrotnie metodę). |

## 🛠️ Warstwa Techniczna

### Interfejsy Zdalne
Logika oparta jest na wspólnej bibliotece `sewagelib`, definiującej kontrakty:
```java
// Przykład interfejsu Domu
public interface IHouse extends Remote {
    // Cysterna wywołuje tę metodę zdalnie, jakby obiekt był lokalny
    int getPumpOut(int max) throws RemoteException;
}