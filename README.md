# CMS Orbit starter for Android

A Jetpack Compose app that reads its pages from [CMS Orbit](https://bifrostorbit.com/products/cms-orbit/), the headless CMS in Bifröst Orbit, with the Kotlin SDK `com.bifrostorbit:cms-sdk`. It is the finished project from the [Android quickstart](https://docs.bifrostorbit.com/cms/developers/quickstart-android/): pages open by their address, your own composables for the blocks editors place, pages refresh on pull and when the app comes back, and a small drafts server lets your team see unpublished changes in a separate build.

## Set up

1. **Create your copy.** Select **Use this template** on GitHub, or run `gh repo create my-app --template bifrost-orbit/cms-starter-android --private --clone`.
2. **Add your settings.** Open the project in Android Studio, which writes `local.properties`. Add the lines from `local.properties.example` to it, with the API address, your public `cms_pk_` key and region from **Configuration › API keys** and **Configuration › Environments** in CMS Orbit. Git leaves `local.properties` out. Never put a secret key in the app.
3. **Run it.** Change `applicationId` in `app/build.gradle.kts` to one of your own, then run the `app` configuration, or `./gradlew :app:installDebug`.

## What's inside

| File | What it does |
| --- | --- |
| `app/build.gradle.kts` | Reads the settings into `BuildConfig`, and adds the `drafts` build |
| `app/src/main/java/com/northwilds/app/Cms.kt` | The CMS Orbit client |
| `MainActivity.kt`, `PageScreen.kt`, `PageViewModel.kt` | Navigation and one screen for every page: redirects, not found and pull to refresh |
| `PageCache.kt` | Keeps pages for a few minutes, and reads them again when the app returns |
| `blocks/` | The composables: `hero`, `featureSpotlight` and `action`, and the lookup that picks one for each block |
| `drafts-server/` | A Ktor server that holds the secret key and reads drafts for your team's `drafts` build, with a signed webhook route at `/cms-webhook` |

## Make it yours

- Write your composables in `blocks/` and add them to `CmsBlocks.kt`, using the component keys from your own space. **Configuration › Components** lists them.
- To see drafts in the app, run `drafts-server` with `CMS_API_URL`, `CMS_PREVIEW_KEY` and `DRAFTS_TOKEN` in its environment (`./gradlew run` in that folder; it stops and names any that are missing), set `drafts.apiUrl` and `drafts.token` in `local.properties`, and build the `drafts` variant.
- To refresh the app within seconds of publishing, point a webhook at the server's `/cms-webhook`, set `CMS_WEBHOOK_SECRET` (until you do, the route answers `503`), and fill in `sendRefreshMessage` with the Firebase Admin SDK. The [quickstart](https://docs.bifrostorbit.com/cms/developers/quickstart-android/) explains both.

Needs Android Studio with JDK 17 or later, Android 7.0 (API 24) or later on the device, and `com.bifrostorbit:cms-sdk` 0.2.

## Licence

MIT. See [LICENSE](LICENSE).
