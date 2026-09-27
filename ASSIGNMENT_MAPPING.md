# Assignment Requirement Mapping

| PDF requirement | Implementation |
|---|---|
| Kotlin Android | Kotlin + Jetpack Compose |
| Live time/date | `HomeContent()` |
| 5–7 home apps | First 7 cached launchable apps |
| A–Z right bar | `AlphabetBar()` |
| Real installed apps | `AppRepository` + `PackageManager` |
| Android 11+ visibility | `<queries>` in manifest |
| Smooth curve | Gaussian distance falloff on Canvas |
| 60 fps target | Lightweight Canvas math; no PackageManager work on touch |
| Letter bubble | Circular Canvas bubble next to finger |
| Filtered list | `LauncherViewModel.appsFor()` |
| Case-insensitive matching | `uppercaseChar()` |
| Alphabetical sort | Repository sorts by lower-case label |
| Empty letters | `No apps` state |
| Release animation | Compose spring |
| App launch | Stored launch Intent + `startActivity()` |
| Cached app list | ViewModel holds loaded list |
| Default launcher bonus | `CATEGORY_HOME` + `CATEGORY_DEFAULT` |
| Spring physics bonus | Compose `spring()` |
| Hide empty letters bonus | Empty letters are dimmed |
| Tests bonus | `LetterLogicTest` |
| Dark theme bonus | Material dark color scheme |
