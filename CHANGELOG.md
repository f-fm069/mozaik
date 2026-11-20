# Changelog

Versionen bis 1.1 stammen aus IPVIO Stream.

## 1.2 – 2026-11-21
**New main menu**
- Sidebar: the logo is 10 % larger; Home, Live TV, Movies, Series, Sport and History sit in the vertical middle; Settings is separate at the bottom, without a divider line (with the bar at the top, Settings is at the right end)
- "History" now has four tabs at the top: History, Favourites, My list and EPG. Favourites, My list and EPG are no longer separate menu items; their pages and functions are unchanged. The EPG tab opens the TV guide in the player, as before
- The "Favourites" and "My list" tiles on the home page jump to the matching tab in History; "History" in the menu always opens on its first tab
- Back from History, Favourites or My list goes to the tab bar first, then to the sidebar (like Movies and Series)
- Settings > Menu: Settings can no longer be moved and always stays at the bottom
- **One-time reset:** the menu order and the hidden entries you set yourself are reset to the new default once, because the old entries (EPG, Favourites, My list) no longer exist. You can change the menu again under Settings > Menu

**Other changes**
- Posters: a movie with no match is searched again without the year
- Parental control: removing the PIN now needs the current PIN (idea from IPVIO Stream `c9207de`, implemented independently for Mozaik)
- In-app update: the download must use https, is checked for completeness and against the SHA-256 checksum in the release asset `Mozaik.apk.sha256`; a download that is incomplete or does not match is discarded (idea from IPVIO Stream `68a15b9`, implemented independently for Mozaik)
- Images: on low-memory devices at most two images are decoded at a time (idea from IPVIO Stream `61782b3`, implemented independently for Mozaik)

## 1.1.1 – 2026-11-15
- Live TV: hidden categories are now also left out of the channel list inside the player; the player uses the same order as the Live TV list.
- Playlists: categories can be switched on and off per type (Live TV, Movies, Series), with search, "All on/off" and channel count. Hiding in Live TV and in Settings now uses the same store; earlier settings are carried over.
- Live TV: a category can be moved with Up/Down ("Move with Up/Down"); new menu entry "Move to end". Move up/down/to top now only work among visible categories.
- Live TV: Back in the channel list goes to the selected category; OK and Right open the channel list more reliably on Fire TV (focus fix).
- Colour theme: new installs start with Ocean; existing installs keep their theme.
- Look: TV banner with a soft radial gradient; context menus are fully opaque so text behind no longer shows through.

## 1.1.0 – 2026-11-15
**Categories** (ported from IPVIO Stream 1.4.2, GPL-3.0)
- Hide categories in Live TV, Movies and Series: hold OK (or press the menu key) on a category and choose "Hide category"; hidden ones are listed under "Hidden" at the end and can be brought back from there
- Sort categories: provider order, A–Z or your own order; move a category up, down or to the top from the same menu
- Home, Movies and Series rows follow the category order and leave out hidden categories
- Settings > Genres & categories: search categories (e.g. "EU | DE"), switch many at once and save them together with "Apply"; ignore whole groups by the provider's tag ("TR |", "[EN]") without reloading the playlist; "Turn all back on"
- Switching many categories no longer stutters: the list is filtered in the background

**Movies and series straight to categories** (idea and setting from IPVIO Stream 1.4.2, GPL-3.0; poster rows are new in Mozaik)
- "Movies" and "Series" in the menu open the categories right away: "Continue watching" on top (only movies or only series, hidden when nothing is started, hold OK to remove), below it every category as a row of posters with "Show all"
- The rows follow the category settings: hidden and ignored categories are left out, the chosen order applies (provider, A–Z, own order); hold OK or press the menu key on a row to hide or move it, "Hidden categories" at the end brings them back
- Many categories: the first 15 rows are built at once, more follow while scrolling
- Back leads to the area's home page; "Categories" at the top still opens the category list
- Settings > General > "Movies and series straight to categories" (on by default); off restores the previous behaviour
- The area's home page no longer shows "Popular movies/series" and "Recently watched"; "Continue watching" stays

**Diagnostics**
- Settings > Diagnostics > "Channel switching (measurement)": time from switching a Live TV channel to the first picture in milliseconds, split into steps (preparation, connection, first data, format detected, decoder ready, first picture, playing); last 20 switches with median and average per stream type (TS, HLS, other) and a reset button. Only times and the stream type are stored, on the device only

**Live TV: faster channel switching**
- The automatically learned buffer no longer stays large forever: it starts at the normal level every time the app starts, takes effect at once (also while zapping, not only for the next title) and drops one level after 10 minutes of playback without stuttering. Repeated stuttering still makes it grow, up to the previous limit (6.5 s start buffer)
- Settings > Player > "Fast channel switching" (off by default): live channels start with 1 second buffered instead of 1.5 to 2.5 seconds; movies, series and recordings are unchanged
- Settings > Player > "Keep last picture while switching" (on by default): when switching channels in Live TV the last picture of the old channel stays on screen until the new channel shows its first picture, instead of going black; the old sound stops at once. After 4.5 seconds without a new picture, or on an error, the normal loading or error view appears

**Home**
- "Recently added" is hidden by default (button on Movies/Series and the rows of that name); turn it back on in Settings > General > Show "Recently added"

**Look**
- New accent colour indigo instead of orange; if you had kept the old orange as your own colour it switches to indigo once. The theme "Orange" can still be chosen in Settings > Design
- New Mozaik logo (app icon, Android TV / Fire TV banner, logo in the app)
- Quick tiles on the home page use the accent colour instead of one colour per area

**Settings**
- New settings start page: all 16 tiles in one grid without group headings, dark tiles with a coloured icon circle in rainbow colours (like the original IPVIO Stream)

**Installation**
- Downloader code `7525746` for Fire TV (Downloader app): it loads the newest `Mozaik.apk` from GitHub Releases. Install steps in the README, FAQ and release notes

## IPVIO Stream 1.1 (before the fork)
**Updates and your data**
- In-app updates from GitHub Releases: the app checks once a day and shows "Update available" in the bell; one click downloads and installs
- Release signing via `keystore.properties` (see docs/RELEASE.md) so updates install over the old version and keep history, favorites and settings

**Playlists**
- Xtream: if `get.php` fails or returns no playlist (e.g. custom error codes like 884), the app loads the account through `player_api.php` automatically
- Several playlists at the same time: "Also show" in Settings > Playlists; movies and series merged (duplicate movies once, same series as one series with several sources), "Source" filter in Movies/Series, source switch in Live TV
- Stalker/Ministra portals (portal address + MAC): live channels and movies
- M3U file from the device (file picker) or uploaded from the phone page

**Live TV**
- Rewind live TV (optional on-device buffer, 256 MB – 2 GB), pause without missing anything, "Back to live"
- Record the current show (from the buffer, no second connection), storage internal or USB, recordings list in Live TV
- Endless reconnect for live streams, automatic reconnect when the server ends a stream
- New "Sports" section: sports live now and coming up (from the TV guide), all sports channels

**Player**
- MPEG-DASH (.mpd) streams; unsupported formats show a message instead of crashing

**Interface**
- View options moved from the header to Settings > View, with a live preview of card size and artwork
- Sidebar on the left, right or top; icons only or always with text
- Phones: landscape by default, tap for controls, double-tap left/right to skip

**Sports, playlists, second stream**
- Sports hub with filters: sport (football, basketball, tennis, F1, MMA, ...) and time (now, today, tomorrow, next 48 h)
- In the Live TV list you can switch the playlist (or "All playlists") without going into the settings
- Second stream in the live player: picture-in-picture or split screen, sound from either stream, swap streams. Arrangement, window size (4 sizes) and corner are saved (Settings > Player > Second stream)

**Languages**
- Dutch (Nederlands) is the 10th language

**Metadata**
- Own API keys for TMDB and OMDb in Settings > API keys
- Without keys: description, cover, rating, genres, running time and trailer straight from the Xtream provider
- OMDb ratings also without TMDB (lookup by title and year)
- Rating order for sorting and "Top rated": IMDb, Rotten Tomatoes, TMDB, provider

**Interface (new)**
- Settings > View rebuilt: 5 tabs (cards, sidebar, grid, home, categories) with a live preview of every option
- New "Donate" card in the settings (PayPal link and QR code)

**Fixes**
- Crashes fixed: duplicate list entries (same channel/show twice) in Sports, Live TV, episodes and cast no longer crash the app
- Live TV list: playlist switch and OK/right-key focus fixed

**Transfer**
- Transfer history, favorites and settings to another device over Wi-Fi (optionally with playlists)
- QR codes: scan instead of typing the address (add playlist from phone, Wi-Fi transfer, Google sign-in)
- Settings > About: optional donation link (PayPal) with QR code
- Much faster loading of large Xtream accounts: series episodes are fetched when a series is opened instead of 50,000 requests while loading
- Optional backup to Google Drive (sign-in with a code, see docs/GOOGLE_DRIVE.md)
- Read the last crash report on the phone (no clipboard needed)

## 1.0 - first open-source release
- Android TV / Google TV / Fire TV IPTV player for Xtream Codes and M3U playlists
- Home, Movies, Series, Live TV with TV guide and catch-up
- Cinema and grid browse views, filters, search with typo tolerance
- Player with audio/subtitle selection, FFmpeg audio decoder, skip intro/credits, source fallback
- Profiles, parental controls, history, my list, favourites, backup and restore
- 9 languages, 12 colour themes
- Licensed under GPL-3.0
