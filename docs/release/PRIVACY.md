# Privacy Policy

_Last updated: 2026-11-14 · Applies to Mozaik 1.0.0_

Mozaik is a free, open-source video player for Android TV and Fire TV. It has no user accounts, shows no advertising and includes no analytics, tracking or crash-reporting service.

## What is stored on your device

Everything you enter in the app is stored on your device only:

- **Playlists and logins.** Playlist addresses and any login data (for example Xtream user name and password) are stored encrypted (AES-GCM with the Android Keystore).
- **API keys.** Your optional TMDB and OMDb keys are stored in the app's private storage without additional encryption. Only the app itself can read that storage.
- **Cached playlist files.** Downloaded playlist data is kept in the app's private storage and is not encrypted. For Xtream sources this data contains the stream addresses, user name and password. Only the app itself can read it.
- **Favourites, history, settings and the programme-guide cache.**

The developer has no access to any of this data and receives none of it.

## Connections the app makes

The app talks to servers you set up or choose to use:

- **Your playlist or IPTV provider.** To load channels, films and series, the app connects to the addresses you entered. Your provider receives your IP address and your login data. Their privacy policy applies.
- **TMDB (optional).** With your own free TMDB API key, the app requests posters, descriptions, ratings and trailers info for the titles you browse. TMDB receives the title being looked up, your API key and your IP address. See https://www.themoviedb.org/privacy-policy.
- **OMDb (optional).** With your own OMDb key, the app requests IMDb and Rotten Tomatoes ratings. OMDb receives the title, your key and your IP address.
- **YouTube (optional).** Only when you open a trailer, the video is played from YouTube, which then receives your IP address.
- **Programme-guide (EPG) sources you configure.** As for playlists.
- **Google Drive.** Not available in this release (no Google sign-in is built in).

## Local network

When you open "Add playlist", the app starts a small web server in your Wi-Fi network (ports 8765 to 8775, protected by a 4-digit code) so you can send a playlist from your phone. It is reachable only inside your local network and only while that screen is open.

## Permissions

Mozaik asks for: network access, start after boot (autostart), keeping the device awake during playback, display over other apps (for the start after boot), notifications and exact alarms (reminders for programmes), installing packages (in-app update), and a foreground service (recordings). It does not use contacts, location, camera or microphone. Voice search uses the speech recognition of your system.

## Backups

The app disables the Android cloud backup. The app's own backup file contains neither playlists nor keys. If you use the Wi-Fi transfer, the data sent contains playlists with their login data, so use it only on trusted networks.

## Deleting your data

Removing the app deletes all of its data, except recordings you saved on external (USB) storage, which you delete yourself.

## No content provided

The developer does not provide any content. See [DISCLAIMER.md](DISCLAIMER.md).

## Contact

Questions: open an issue at https://github.com/f-fm069/mozaik/issues.

## Changes

If this policy changes, the new version is published in this repository with a new date.
