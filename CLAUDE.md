# Viewon – Hinweise für Claude

## Datenschutz (hat Vorrang vor allem anderen)

- Mein Klarname und meine E-Mail-Adresse stehen nie im Projekt und werden nie hochgeladen:
  nicht in Dateien, Copyright-Zeilen, README, Doku, Commit-Nachrichten, Autorenfeldern,
  Zweignamen und nicht in lokalen Pfaden wie /Users/<Name>/.
- Die eigene Copyright-Zeile heißt: Copyright (C) 2026 Viewon contributors
- Vor jedem Commit und jedem Hochladen prüfst du den Änderungsumfang auf Name, E-Mail-Adresse
  und lokale Benutzerpfade und sagst es mir, falls du etwas findest.
- Dazu gehört auch, welcher Autor in Git eingestellt ist (git config user.name / user.email).
  Ist dort nichts oder ein Klarname eingestellt, nicht committen, sondern mir Bescheid sagen.
- Frag mich nie nach meinem Klarnamen oder meiner E-Mail-Adresse.

## 1. Projekt

Viewon ist mein eigenständig weiterentwickelter Fork von Emrodev/IPVIO-Stream:
ein IPTV-Player für Android TV und Fire TV. Die App bringt keine eigenen Inhalte mit.
Lizenz: GPL-3.0.

## 2. Regeln

- LICENSE, Copyright-Hinweise, THIRD_PARTY_NOTICES.md und den Ordner licenses/ nicht entfernen.
- Den Namen „IPVIO“ und das IPVIO-Logo in meiner Version nicht verwenden.
- Keine Zugangsdaten, Schlüssel, Playlists oder Server-URLs ins Projekt schreiben.
- Nie etwas zu Emrodev/IPVIO-Stream hochladen und dort nie einen Pull Request anlegen.
- Nur nach Rückfrage hochladen.
- Änderungen auf einem eigenen Zweig machen und mir danach erklären, was geändert wurde.

## 3. Arbeitsweise

- Antworte auf Deutsch in einfachen Worten, ich bin Anfänger.
- In kleinen Schritten arbeiten: erst bauen und testen, dann weiter.

## 4. Offene Aufgaben

- [x] Umbenennung von IPVIO zu Viewon
  - [x] Etappe 1: sichtbarer Name in der App (Texte, Übersetzungen, App-Name)
  - [x] Package-Name ändern
  - [x] Neues Logo
  - [x] Update-Funktion auf meinen Fork umstellen
- [x] README überarbeiten und Fork-Hinweis ergänzen
- [x] Spendenlink in .github prüfen (FUNDING.yml und Spendenbereich entfernt)
- [ ] Kategorien ausblenden

## 5. Bauen und Testen

- Bauen: `bash ./gradlew assembleDebug` (gradlew ist auf diesem Mac nicht ausführbar)
- Tests: `bash ./gradlew test`
- Bekannt: PhoneImportTest schlägt schon ohne Änderungen fehl.
