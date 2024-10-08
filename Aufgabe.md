# Vertragsverwaltung

## Zusammenfassung
Der Auszubildende entwickelt eine Java-Anwendung die JSON-Dateien einliest mit denen es möglich ist (einfache)
Versicherungsverträge anzulegen, zu ändern und zu stonieren und eine Preisberechnung
für neue Verträge und Änderungen zur Verfügung zu stellen.
Um die Verträge zu speichern wird der Auszubildende sie in einem JSON-File speichern und beim Aufrufen des Vertrages
von der Festplatte lesen (keine Datenbank).
Neben Java wird der Auszubildende Maven nutzen. Die Nutzung von Java Bibliotheken außerhalb des JDKs muss mit dem
Ausbilder abgesprochen werden und wird so gering gehalten wie möglich (z.B. kein Spring).
Als IDE nutzt der Auszubildende Intellij.

## Ziel
Der Auszubildende soll bei dieser Übung die grundlegende Handhabung von Java erlernen und vertiefen. Im
späteren Verlauf seiner Ausbildung soll dieses Projekt mit Java Bibliotheken (Spring, JPA) und Datenbanken,
Funktionen und einem Front-End erweitert werden.

# Aufgabenteile

## JSON Funktionen
Folgende Funktionen sollen über die Konsole möglich sein. Dabei imitieren die JSON Dateien eine Rest-Schnittstelle,
die nach Abschluss des Praxis Transfer Projekts das Einlesen über Dateien ablösen wird.
GET /vertraege -> gibt alle gespeicherten Verträge zurück
GET /vertraege/{vsnr} -> gibt einen bestimmten Vertrag zurück
POST /preis -> Empfängt einen Vertrag im JSON Format und gibt einen Preis zurück. (Nutzbar bei neuen oder zu
ändernden Verträgen)
POST /neu -> Empfängt einen neuen Vertrag im JSON Format, legt ihn an und gibt die neue VSNR zurück
POST /aenderung -> Empfängt einen geänderten Vertrag, überschreibt den alten, berechnet den Preis neu und gibt ihn
zurück
DELETE /vertrage/{vsnr} -> löscht einen Vertrag *ohne* Prüfung aus dem System


## Vertragsdaten
die zu speichernden Verträge enthalten folgende Daten
- VSNR: Nummerisch, 8 Stellen
- Preis
- Versicherungsbeginn
- Versicherungsablauf
- Antrags Datum
### Fahrzeugdaten
- Amtliches Kennzeichen
- Fahrzeug Hersteller (z.B. BMW, Mercedes)
- Fahrzeug Typ (z.B. GLA, Focus, X1)
- Fahrzeug Höchstgeschwindigkeit: Bis maximal 250kmh
- Wagnisskennziffer: Es wird NUR WKZ 112 zugelassen. Alle anderen sollen auf Fehler laufen
### Partnerdaten (Partner ist der Versicherungsnehmer)
- Der Versicherungsnehmer ist mit dem Fahrzeughalter identisch. Es gibt immer nur einen Partner pro Vertrag
- Vorname
- Nachname
- Addresse (Bundesland, Stadt, Straße, Hausnummer, PLZ): Andere Länder außer DE laufen ebenfalls auf Fehlermeldung
- Geburtsdatum

## Preisberechnung
- Der Preis eines Vertrags berechnet sich aus dem Geburtsdatum des Halters und der Höchstgeschwindigkeit des Fahrzeugs

## Mitte vom Projekt: Erinnerung Gitvorträge