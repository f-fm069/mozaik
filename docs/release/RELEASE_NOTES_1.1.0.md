# Mozaik 1.1.0

Mozaik 1.1.0 brings tidier categories, a faster way into films and series, quicker channel switching and a fresh look.

## What's new

- **Hide and sort categories** in Live TV, Movies and Series: hold OK (or press the menu key) on a category. Hidden categories are listed under "Hidden" at the end and can be brought back. Sort by provider order, A–Z or your own order.
- **Movies and series straight to categories:** "Continue watching" on top, below it every category as a row of posters. Can be turned off in Settings > General.
- **Faster channel switching in Live TV:**
  - The learned buffer gets smaller again after 10 minutes without stuttering and starts at normal every time the app starts.
  - Optional "Fast channel switching" (Settings > Player, off by default).
  - "Keep last picture while switching" (on by default): no black screen between channels.
  - New measurement in Settings > Diagnostics > "Channel switching (measurement)".
- **New look:** accent colour indigo and a new Mozaik logo. The orange theme can still be chosen in Settings > Design.
- **New settings start page:** all settings tiles in one grid with coloured icons.
- **"Recently added"** is now hidden by default (turn it back on in Settings > General).
- **Downloader code `7525746`** for an easy install on Fire TV.

The full list is in [CHANGELOG.md](../../CHANGELOG.md).

## Install

Mozaik is a **TV app**, made for Android TV, Google TV and Amazon Fire TV and their remote controls. It is not a phone app.

**Fire TV (Downloader app, code `7525746`):**

1. Install the app **Downloader** from the Amazon Appstore.
2. Allow Downloader to install apps from unknown sources: Settings > My Fire TV > Developer options > Install unknown apps > Downloader.
3. Open Downloader and enter the code **`7525746`** (or `https://github.com/f-fm069/mozaik/releases/latest/download/Mozaik.apk`).
4. The APK downloads; choose **Install**.

**Android TV / Google TV:** use the Downloader app the same way (code `7525746` or the address above), or copy `Mozaik.apk` to the device (USB or file-manager app) and open it. Allow "Install unknown apps" for the installing app when asked.

**Updating from 1.0.0:** install 1.1.0 over the old version (or use the update in the app's bell). History, favourites and settings are kept, because the APK is signed with the same key.

Mozaik does not provide any content. Add your own playlist in the app. See [DISCLAIMER.md](DISCLAIMER.md).

## Checksum

Verify the file (optional but recommended): the SHA-256 checksum is in `Mozaik.apk.sha256`.

SHA-256 of `Mozaik.apk`: `a32fa27665b6e0dfd4e6853f88c02a45005bd0c2e099daee700dfb15379b70db`

Signing certificate: SHA-256 `4efe05232eaecae57fe8c57307b5572ec4cb0bd7bf63a17fd7e1ea8160714128` (same as 1.0.0)

## License and credits

Mozaik is free software under the GNU General Public License version 3 (GPL-3.0). Based on [IPVIO-Stream](https://github.com/Emrodev/IPVIO-Stream) by Emrodev (GPL-3.0). Film data and images by TMDB. See the README for credits of the sample films and [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) for the libraries used.

Mozaik is provided without any warranty. It does not provide any content; you are responsible for the playlists you add. See [DISCLAIMER.md](DISCLAIMER.md).
