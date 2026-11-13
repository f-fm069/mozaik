# Releasing a new version

Mozaik checks the latest GitHub release once a day and offers the update in the bell at the top right.
Android installs an update **over** the old app (and keeps playlists, favorites, history and settings) only if
both APKs are signed with **the same key**. Lose the key and every user has to uninstall and start from scratch.

## 1. One-time: the signing key

### If version 1.0 was built with "Build APK" in Android Studio (debug key)

That APK is signed with the debug key of your computer:
`C:\Users\<you>\.android\debug.keystore` (Windows) or `~/.android/debug.keystore` (macOS/Linux).
To let 1.0 users update without losing anything, keep signing with exactly this key:

1. Copy `debug.keystore` to a safe place **outside** the project (and make a backup, e.g. on a USB stick).
2. Create `keystore.properties` in the project root (it is ignored by Git):

```properties
storeFile=C:/keys/mozaik-debug.keystore
storePassword=android
keyAlias=androiddebugkey
keyPassword=android
```

### Starting fresh with your own key (recommended for the future)

Create the key **outside** the project folder, with a neutral name in the certificate (`-dname "CN=Mozaik"`: no personal
name, organisation or place). `keytool` then asks only for the passwords; type them yourself and never write them into a
file that is checked in:

```
mkdir -p ~/keys
keytool -genkeypair -v -keystore ~/keys/mozaik-release.jks -alias mozaik -keyalg RSA -keysize 4096 -validity 36500 -dname "CN=Mozaik"
```

Then create `keystore.properties` in the project root (ignored by Git) with the absolute path to that file, the alias
and the passwords. Alternatively leave the passwords out of the file and set environment variables instead:
`MOZAIK_STORE_FILE`, `MOZAIK_STORE_PASSWORD`, `MOZAIK_KEY_ALIAS`, `MOZAIK_KEY_PASSWORD` (a value in the file wins).

Back up `mozaik-release.jks` and its passwords in a safe place (e.g. a password manager and a USB stick). Without it no
update can be installed over an existing installation.
Users of an APK signed with a different key have to uninstall once (Settings > Storage > Create backup first, then restore).

**Never commit the key or `keystore.properties`.** Both are already in `.gitignore`.

## 2. Every release

1. In `app/build.gradle.kts` raise `versionCode` (by 1) and `versionName` (e.g. `"1.2"`).
2. Build the signed APK: `gradlew assembleRelease` (or Build > Generate Signed App Bundle / APK).
   The file is `app/build/outputs/apk/release/app-release.apk`.
3. Copy it twice: **`Mozaik-<version>.apk`** (e.g. `Mozaik-1.2.apk`) and an identical **`Mozaik.apk`**.
4. On GitHub: **Releases > Draft a new release**
   - Tag: `v` + versionName, e.g. `v1.2` (the app compares this tag with its own version)
   - Title and notes: what is new (the first lines appear in the app)
   - Attach both files and the `.sha256` file. `Mozaik.apk` must be there with exactly this name: the README and
     Downloader link `…/releases/latest/download/Mozaik.apk` and the in-app update use it.
   - **Publish release** (not "pre-release", the app ignores those)

The download link in the README always points to the newest release:
`https://github.com/f-fm069/mozaik/releases/latest/download/Mozaik.apk`

### Checklist

- [ ] Raise `versionCode` and `versionName` in `app/build.gradle.kts`
- [ ] Build the signed release APK and save it as `Mozaik-<version>.apk` plus an identical copy `Mozaik.apk`
- [ ] Calculate the SHA-256 checksum: `shasum -a 256 Mozaik-<version>.apk` (macOS/Linux) or `certutil -hashfile Mozaik-<version>.apk SHA256` (Windows)
- [ ] Create the GitHub release (https://github.com/f-fm069/mozaik/releases/latest) with **both** APK files and the `.sha256` file, and add the SHA-256 checksum to the notes
- [ ] Check that `https://github.com/f-fm069/mozaik/releases/latest/download/Mozaik.apk` downloads the new version (this is also the Downloader address)

## 3. What users see

- Within a day the bell shows a red dot and "Update available: version 1.2". One click downloads and opens the installer.
- Settings > About > App update: check now, install, or turn off the automatic check.
- The first time, Android asks to allow "Install unknown apps" for Mozaik.

Version 1.0 has no update check yet, so 1.0 users install 1.1 once by hand (Downloader code or link). From 1.1 on it works from inside the app.
