# Third-party notices

Mozaik is licensed under the GNU General Public License v3 (see `LICENSE`). Mozaik is a fork of [IPVIO Stream](https://github.com/Emrodev/IPVIO-Stream) by Emrodev, which is also licensed under the GPL-3.0. Mozaik uses the following third-party components.

## Libraries

| Component | License |
|---|---|
| AndroidX (Activity, Compose UI/Foundation, Work) | Apache License 2.0 |
| AndroidX Media3 (ExoPlayer, HLS, DASH, UI) | Apache License 2.0 |
| Jellyfin Media3 FFmpeg decoder (`org.jellyfin.media3:media3-ffmpeg-decoder`) | GNU GPL v3 (contains FFmpeg) |
| OkHttp (Square) | Apache License 2.0 |
| Coil (`io.coil-kt:coil-compose`) | Apache License 2.0 |
| Kotlin standard library and coroutines (JetBrains) | Apache License 2.0 |
| JUnit 4 (tests only) | Eclipse Public License 1.0 |

The libraries are downloaded by Gradle at build time and are not stored in this repository. Their licence texts are available from the respective projects.

### Full list of libraries in the release build (version 1.0.0)

Resolved from the `releaseRuntimeClasspath` (direct and transitive dependencies).

| Library (group:artifact) | Version | License |
|---|---|---|
| `androidx.activity:activity-compose` | 1.13.0 | Apache License 2.0 |
| `androidx.compose:compose-bom` (Compose UI 1.12.1, Foundation, Animation, Runtime) | 2026.09.00 | Apache License 2.0 |
| `androidx.media3:media3-exoplayer`, `-exoplayer-hls`, `-exoplayer-dash`, `-ui` (and their Media3 modules) | 1.11.1 | Apache License 2.0 |
| `androidx.work:work-runtime-ktx` | 2.10.0 | Apache License 2.0 |
| `androidx.room:room-runtime` (via WorkManager) | 2.6.1 | Apache License 2.0 |
| Other AndroidX modules pulled in by the above (core 1.18.0, lifecycle 2.9.4, savedstate, collection, annotation, appcompat-resources 1.6.1, window, emoji2, startup, profileinstaller, sqlite, tracing, navigationevent, recyclerview, vectordrawable and others) | various | Apache License 2.0 |
| `org.jellyfin.media3:media3-ffmpeg-decoder` | 1.9.0+1 | GNU GPL v3 (contains FFmpeg) |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | Apache License 2.0 |
| `com.squareup.okio:okio` | 3.9.0 | Apache License 2.0 |
| `io.coil-kt:coil-compose` (with coil, coil-base, coil-compose-base) | 2.7.0 | Apache License 2.0 |
| `com.google.accompanist:accompanist-drawablepainter` | 0.32.0 | Apache License 2.0 |
| `com.google.guava:guava` (with failureaccess 1.0.2, listenablefuture) | 33.3.1-android | Apache License 2.0 |
| `org.jetbrains.kotlin:kotlin-stdlib` | 2.2.10 | Apache License 2.0 |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` / `-core` | 1.9.0 | Apache License 2.0 |
| `org.jetbrains.kotlinx:kotlinx-serialization-core` | 1.7.3 | Apache License 2.0 |
| `org.jetbrains:annotations` | 23.0.0 | Apache License 2.0 |
| `org.jspecify:jspecify` | 1.0.0 | Apache License 2.0 |

All licences above are compatible with distributing Mozaik under the GPL-3.0 (Apache License 2.0 code may be included in GPL-3.0 works).

## Fonts

| Font | License | File |
|---|---|---|
| Outfit (The Outfit Project Authors) | SIL Open Font License 1.1 | `licenses/OFL_Outfit.txt` |
| Inter (The Inter Project Authors) | SIL Open Font License 1.1 | `licenses/OFL_Inter.txt` |

The font files in `app/src/main/res/font/` are subsets/copies of the original fonts and remain under the OFL.

## Images and icons

| Item | Source / License |
|---|---|
| Mozaik logo and app icon (`res/drawable`, `res/mipmap-*`, `docs/logo-horizontal-dark.png`) | Created for Mozaik, GPL-3.0 like the rest of the project |
| In-app symbols (`AppIcons.kt`) | Drawn in code, from IPVIO Stream (GPL-3.0) |
| TMDB logo (`res/drawable/tmdb_logo.xml`, `docs/tmdb-logo.svg`) | Official TMDB logo, used for attribution as required by the TMDB API terms of use. Trademark of TMDB, not covered by the GPL. |

## Online services (optional, user-supplied keys)

| Service | Purpose | Notes |
|---|---|---|
| The Movie Database (TMDB) | film and series information, images | *This product uses TMDB and the TMDB APIs but is not endorsed, certified, or otherwise approved by TMDB.* Users must provide their own API key and follow the TMDB terms of use. |
| OMDb API | IMDb and Rotten Tomatoes ratings | Users must provide their own API key. |
| TheIntroDB | intro, recap and credits times | Read access without a key. |
| GitHub Releases API | in-app update check | Can be turned off in Settings > About. |
| Google Drive (appDataFolder) | optional backup of settings | Only after the user signs in. |

IMDb, Rotten Tomatoes, TMDB, Android TV, Google TV and Fire TV are trademarks of their respective owners. Mozaik and IPVIO Stream are not affiliated with or endorsed by them.
