# Mozaik 1.2

Mozaik 1.2 brings a new main menu: History now has tabs for Favourites, My list and EPG, and the sidebar is tidier. The in-app update is safer, and posters and weak devices got small fixes.

## What's new

- **New sidebar:** the logo is 10 % larger. Home, Live TV, Movies, Series, Sport and History sit in the vertical middle, Settings is separate at the bottom.
- **History with tabs:** History, Favourites, My list and EPG are tabs at the top of the History page. Favourites, My list and EPG are no longer separate menu items; their pages and functions are unchanged. The EPG tab opens the TV guide in the player, as before. The "Favourites" and "My list" tiles on the home page jump to the matching tab.
- **Back button:** from History, Favourites or My list, Back goes to the tab bar first, then to the sidebar.
- **Settings > Menu:** Settings always stays at the bottom and can no longer be moved.
- **One-time reset:** your own menu order and hidden entries are reset to the new default once, because the old entries (EPG, Favourites, My list) no longer exist. You can change the menu again under Settings > Menu.
- **Update check:** the app update only downloads over https, checks that the download is complete and compares it with the SHA-256 checksum of the release. A download that is incomplete or does not match is discarded.
- **Parental control:** removing the PIN now needs the current PIN.
- **Posters:** a movie with no match is searched again without the year.
- **Weak devices:** at most two images are decoded at a time on devices with little memory.

The full list is in [CHANGELOG.md](../../CHANGELOG.md).

## Install

Mozaik is a **TV app**, made for Android TV, Google TV and Amazon Fire TV and their remote controls. It is not a phone app.

**Fire TV (Downloader app, code `7525746`):**

1. Install the app **Downloader** from the Amazon Appstore.
2. Allow Downloader to install apps from unknown sources: Settings > My Fire TV > Developer options > Install unknown apps > Downloader.
3. Open Downloader and enter the code **`7525746`** (or `https://github.com/f-fm069/mozaik/releases/latest/download/Mozaik.apk`).
4. The APK downloads; choose **Install**.

**Android TV / Google TV:** use the Downloader app the same way (code `7525746` or the address above), or copy `Mozaik.apk` to the device (USB or file-manager app) and open it. Allow "Install unknown apps" for the installing app when asked.

**Updating from 1.1.1, 1.1.0 or 1.0.0:** install 1.2 over the old version (or use the update in the app's bell). History, favourites and settings are kept, because the APK is signed with the same key.

Mozaik does not provide any content. Add your own playlist in the app. See [DISCLAIMER.md](DISCLAIMER.md).

## Checksum

Verify the file (optional but recommended): the SHA-256 checksum is in `Mozaik.apk.sha256`.

SHA-256 of `Mozaik.apk`: `<fill in after building the release APK>`

Signing certificate: SHA-256 `4efe05232eaecae57fe8c57307b5572ec4cb0bd7bf63a17fd7e1ea8160714128` (same as 1.0.0)

## License and credits

Mozaik is free software under the GNU General Public License version 3 (GPL-3.0). Based on [IPVIO-Stream](https://github.com/Emrodev/IPVIO-Stream) by Emrodev (GPL-3.0). Film data and images by TMDB. See the README for credits of the sample films and [THIRD_PARTY_NOTICES.md](../../THIRD_PARTY_NOTICES.md) for the libraries used.

Mozaik is provided without any warranty. It does not provide any content; you are responsible for the playlists you add. See [DISCLAIMER.md](DISCLAIMER.md).
