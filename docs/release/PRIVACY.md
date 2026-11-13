# Privacy Policy

_Last updated: 2026-11-14 · Applies to Mozaik 1.0.0_

Mozaik is a free, open-source video player for Android TV and Fire TV. It does not create user accounts, does not show advertising and does not include analytics, tracking or crash-reporting services.

## What stays on your device

Everything you enter in the app is stored locally on your device, in the app's private storage:

- the playlist addresses (M3U / Xtream / Stalker) and any login data you enter for them. This list is stored encrypted (AES-GCM with a key held in the Android Keystore);
- the cached playlists and programme guide. For Xtream accounts the stream addresses in this cache contain your user name and password, as your provider sends them; the cache is not encrypted but only readable by the app;
- your optional TMDB and OMDb API keys (not encrypted, only readable by the app);
- favourites, watch history, reminders and settings.

The developer has no access to this data. It is not sent to the developer or to any server operated by the developer.

Android's automatic cloud backup is turned off for Mozaik (`allowBackup="false"`). The app's own backup file (Settings > Storage) contains your settings, favourites and history, but no playlists and no API keys. Only when you deliberately transfer your setup to another device over Wi-Fi, or back it up to your own Google Drive, are the playlists with their login data included.

## Connections the app makes

- **Your playlist or IPTV provider.** To load channels, films, series and the programme guide, the app connects to the addresses you entered and to the addresses contained in your playlist (streams, channel logos, EPG). Your provider receives your IP address and the login data you entered, as with any player. Their privacy policy applies.
- **Update check on GitHub (on by default).** When you start the app, and at most once every 20 hours, the app asks GitHub for the newest release: an HTTPS request to `https://api.github.com/repos/f-fm069/mozaik/releases/latest`. The request contains only the headers `User-Agent: Mozaik` and `Accept: application/vnd.github+json`; no device ID, no playlist and no personal data. GitHub sees your IP address and the time of the request. If you choose to install an update, the APK is downloaded from the GitHub release page. You can turn the automatic check off under Settings > About Mozaik > "Check for updates automatically"; "Check for updates now" then only runs when you press it. See the GitHub privacy statement at https://docs.github.com/site-policy/privacy-policies/github-general-privacy-statement.
- **TMDB (optional).** If you enter your own free TMDB API key, the app asks The Movie Database (`api.themoviedb.org`, images from `image.tmdb.org`) for posters, descriptions, cast and ratings, sending the title and year it is looking up and your API key. TMDB sees your IP address. See the TMDB privacy policy at https://www.themoviedb.org/privacy-policy.
- **TheIntroDB (with TMDB).** When you play a film or an episode that TMDB has identified, and "Offer to skip intro" is on (Settings > Player, on by default), the app asks `https://api.theintrodb.org` for intro and credits times. The request contains the TMDB ID of the title, the season and episode number and the running time; TheIntroDB sees your IP address. No key and no personal data are sent. Turn "Offer to skip intro" off, or do not enter a TMDB key, and no request is made.
- **OMDb (optional).** Only if you enter your own OMDb API key, the app asks `www.omdbapi.com` for IMDb and Rotten Tomatoes ratings, sending the title or IMDb ID and your key.
- **Trailers.** Only when you press "Trailer", the app opens the trailer on YouTube (or a YouTube search) in the YouTube app or browser. YouTube's privacy policy applies.
- **Google Drive backup (optional).** Only after you sign in with your Google account in the app, it stores a backup in the hidden app folder of your own Google Drive. This feature is not available in builds without a configured Google sign-in, including the official 1.0.0 APK.

## Local network

While the "Add playlist" screen is open, the app runs a small web server in your local network (port 8765–8775), so you can send your playlist or login data from your phone's browser. Access requires the four-digit code shown on the TV. The server stops when you leave the screen. The same applies when you transfer your setup to another device over Wi-Fi.

## What the app does not do

- No advertising, no analytics, no crash-reporting service, no third-party tracking.
- No sale or sharing of data. The developer does not collect any data to share.
- No content is provided by the developer (see [DISCLAIMER.md](DISCLAIMER.md)).

## Permissions

| Permission | Why |
|---|---|
| Internet, network state | Load streams, playlists and metadata; reconnect quickly after the network comes back |
| Wake lock | Keep the device awake during playback |
| Start at boot, display over other apps | Optional autostart: open Mozaik after the device starts (Android 10+ needs the overlay permission for this) |
| Notifications, exact alarms | Reminders for programmes you marked |
| Foreground service (data sync) | Keep a live-TV recording running |
| Install packages | Open a downloaded Mozaik update for installation (Android asks you first) |

Mozaik uses no permissions for contacts, location, camera or microphone. Voice search uses Android's own speech recognition, which asks for its own permissions.

## Your control

Remove the app, or clear its data in the Android settings, and all locally stored data is deleted. Recordings you saved to a USB drive stay on that drive until you delete them.

## Contact

Questions: open an issue at https://github.com/f-fm069/mozaik/issues.

## Changes

If this policy changes, the new version is published in this repository with a new date.
