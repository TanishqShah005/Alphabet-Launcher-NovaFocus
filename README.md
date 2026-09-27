# Alphabet Launcher

A minimal Android launcher-style home screen built for the **Android Developer Assignment — Alphabet Launcher**.

## What is implemented

### Core requirements
- Live current time and date.
- Home screen with 7 favourite apps (first 7 launchable apps).
- Real launchable-app discovery through `PackageManager`.
- Android 11+ package visibility via `<queries>` for `MAIN + LAUNCHER`.
- Vertical A–Z bar on the right edge with a star and bottom dot.
- Finger-driven curved/bulged alphabet animation.
- Enlarged selected-letter bubble near the finger.
- Instant letter-based filtering.
- Case-insensitive app grouping and alphabetical sorting.
- Clear `No apps` empty state.
- Spring animation when the finger is released.
- Tapping an app launches the real installed application.
- App list is loaded once and cached in the ViewModel; PackageManager is not queried on every touch.

### Bonus included
- `CATEGORY_HOME` / `CATEGORY_DEFAULT` intent filter so the app can be selected as a home launcher.
- Empty letters are dimmed.
- Basic dark theme.
- No animation library is used for the core curve: the curve is calculated directly from finger distance.

## Architecture

```text
MainActivity
    |
    v
AlphabetLauncherScreen
    |
    +---- HomeContent / FilteredContent
    |
    +---- AlphabetBar
    |       |
    |       +---- touch position -> selected letter
    |       +---- Gaussian distance falloff -> horizontal shift
    |       +---- spring -> release animation
    |
    v
LauncherViewModel
    |
    v
AppRepository
    |
    v
PackageManager
```

## Curve animation

For each letter, its normal x-position is shifted left according to its distance from the finger:

```text
influence = exp(-(distance²) / (2 * sigma²))
x = baseX - maxShift * influence * bend
```

The result is a smooth Gaussian-shaped bulge. The selected letter is the point nearest the finger. `bend` is animated from `0 -> 1` when interaction begins and back to `0` with a spring on release.

The touch path does not call `PackageManager`. App grouping is performed against the cached list already held by the ViewModel.

## Android 11+ package visibility

The manifest declares:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

This lets `queryIntentActivities()` discover launchable applications on Android 11+.

## Setup

1. Install a recent Android Studio version with JDK 17.
2. Open this project in Android Studio.
3. Let Gradle sync.
4. Connect a physical Android phone with USB debugging enabled.
5. Run the `app` configuration.
6. For the bonus launcher behavior, press Home on the device and select **Alphabet Launcher** when Android asks for a home app.

The project uses:
- Android Gradle Plugin 9.4.0
- Kotlin 2.4.10
- Compose BOM 2026.09.00
- compileSdk 37
- targetSdk 36
- minSdk 26

## Libraries

| Library | Version | Why |
|---|---:|---|
| AndroidX Activity Compose | 1.13.0 | Compose activity integration |
| AndroidX Core KTX | 1.17.0 | Android Kotlin extensions and Drawable bitmap conversion |
| AndroidX Lifecycle ViewModel Compose | 2.11.0 | Keeps the cached app list outside the UI |
| Jetpack Compose BOM | 2026.09.00 | Consistent Compose dependency versions |
| Compose UI / Foundation / Animation / Material3 / Icons Extended | BOM-managed | UI, Canvas, gestures, animation, theme and icons |
| JUnit | 4.13.2 | Local unit testing support |

No third-party animation/launcher library is used. The curve is implemented directly in Kotlin/Compose.

## AI/tool disclosure

AI assistance was used during development for:
- interpreting the assignment requirements,
- planning the project structure,
- drafting and reviewing Kotlin/Compose code,
- explaining Android package visibility and the curve calculation.

The final implementation should be reviewed and understood by the developer before submission.

## Submission checklist

### Screen recording
Record 1–3 minutes on a **real Android phone** showing:
1. Resting clock/date screen.
2. Favourite apps.
3. Slow drag down the alphabet.
4. Fast drag across the alphabet.
5. An empty letter.
6. Release and spring-back.
7. Opening an app from the list.
8. If enabled, selecting the app as the default launcher.

### GitHub
- Push the full project.
- Keep the commit history.
- Do not squash everything into one commit.
- Keep this README in the repository.

### Library list
The table above is the library list required by the assignment.
