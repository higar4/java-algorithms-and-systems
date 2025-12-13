package pl.kajaki.model;

public enum StatusRezerwacji {
    ZALOZONA,      // czeka na zaakceptowanie
    POTWIERDZONA,  // organizator potwierdzil
    REALIZOWANA,   // pracownik realizuje
    ZREALIZOWANA,  // wydarzenie zakonczone
    ODWOLANA       // anulowano wydarzenie
}