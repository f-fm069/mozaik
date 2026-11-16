# Mozaik 1.1.1

Mozaik 1.1.1 fixes hidden categories in the player, makes category filtering easier and sets a new default colour theme.

## What's new

- **Live TV:** hidden categories are now also left out of the channel list inside the player.
- **Playlists:** categories can now be switched on and off per type (Live TV, Movies, Series), with search, "All on/off" and channel count. Hiding in Live TV and in Settings now uses the same store; your earlier settings are carried over.
- **Colour theme:** new installs start with Ocean. Existing installs keep their theme.

The full list is in [CHANGELOG.md](../../CHANGELOG.md).

## Install

Mozaik is a **TV app**, made for Android TV, Google TV and Amazon Fire TV and their remote controls. It is not a phone app.

**Fire TV (Downloader app, code `7525746`):**

1. Install the app **Downloader** from the Amazon Appstore.
2. Allow Downloader to install apps from unknown sources: Settings > My Fire TV > Developer options > Install unknown apps > Downloader.
3. Open Downloader and enter the code **`7525746`** (or `https://github.com/f-fm069/mozaik/releases/latest/download/Mozaik.apk`).
4. The APK downloads; choose **Install**.

**Android TV / Google TV:** use the Downloader app the same way (code `7525746` or the address above), or copy `Mozaik.apk` to the device (USB or file-manager app) and open it. Allow "Install unknown apps" for the installing app when asked.

**Updating from 1.1.0 or 1.0.0:** install 1.1.1 over the old version (or use the update in the app's bell). History, favourites and settings are kept, because the APK is signed with the same key.

Mozaik does not provide any content. Add your own playlist in the app. See [DISCLAIMER.md](DISCLAIMER.md).

## Checksum

Verify the file (optional but recommended): the SHA-256 checksum is in `Mozaik.apk.sha256`.

SHA-256 of `Mozaik.apk`: `4265c7a0596fb0b7384424c7afa27c6a63a3f28326b072402386e74b57d641c8`

Signing certificate: SHA-256 `4efe05232eaecae57fe8c57307b5572ec4cb0bd7bf63a17fd7e1ea8160714128` (same as 1.0.0)

## License and credits

Mozaik is free software under the GNU General Public License version 3 (GPL-3.0). Based on [IPVIO-Stream](https://github.com/Emrodev/IPVIO-Stream) by Emrodev (GPL-3.0). Film data and images by TMDB. See the README for credits of the sample films and [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) for the libraries used.

Mozaik is provided without any warranty. It does not provide any content; you are responsible for the playlists you add. See [DISCLAIMER.md](DISCLAIMER.md).
