# Interview Walkthrough Notes

## 1. Why Jetpack Compose?
Compose makes the custom alphabet animation straightforward because the A–Z bar can be drawn directly on a `Canvas`, while the app list uses normal Compose layouts.

## 2. How are installed apps discovered?
`AppRepository` creates an `ACTION_MAIN + CATEGORY_LAUNCHER` intent and asks `PackageManager.queryIntentActivities()` for matching activities.

The manifest contains a matching `<queries>` intent because Android 11+ filters package visibility.

## 3. Why is PackageManager not called during dragging?
The assignment explicitly asks for the app list to be loaded and cached once. The repository loads the list once when the ViewModel is created. Touch movement only performs math against that cached list.

## 4. How does the curve work?
Each letter has a normal x/y position. The y-distance from the finger is passed through a Gaussian function:

`influence = exp(-(distance²) / (2 × sigma²))`

The closest letters move most, and letters farther away move less.

`x = baseX - maxShift × influence × bend`

## 5. How is the selected letter calculated?
The finger's y-coordinate is normalized to the A–Z area:

`normalized = (fingerY - top) / height`

Then:

`index = normalized × 26`

The index is clamped to A–Z.

## 6. What happens on release?
The bend value is animated back to zero using a Compose spring animation.

## 7. How is an app launched?
The cached app model stores a launch `Intent`. Clicking an app row calls `startActivity()` with that intent.

## 8. Why are empty letters dimmed?
It gives the user a visual indication that a letter currently has no matching app while keeping the full A–Z bar visible.

## 9. What would you improve next?
Possible extensions are search, persistent custom favourites, live package-change refresh, haptic ticks, and more extensive tests. The assignment says bonus work should come after the core requirements are solid.
