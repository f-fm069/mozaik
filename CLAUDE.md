# Viewo – Hinweise für Claude

## 1. Projekt

Viewo ist mein eigenständig weiterentwickelter Fork von Emrodev/IPVIO-Stream:
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

- [ ] Umbenennung von IPVIO zu Viewo
- [ ] Package-Name ändern
- [ ] Neues Logo
- [ ] README überarbeiten und Fork-Hinweis ergänzen
- [ ] Spendenlink in .github prüfen
- [ ] Update-Funktion auf meinen Fork umstellen
- [ ] Kategorien ausblenden

## 5. Bauen und Testen

- Bauen: `bash ./gradlew assembleDebug` (gradlew ist auf diesem Mac nicht ausführbar)
- Tests: `bash ./gradlew test`
- Bekannt: PhoneImportTest schlägt schon ohne Änderungen fehl.
