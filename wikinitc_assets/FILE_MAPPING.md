# File Mapping — NitcWiki Redesign Assets

Each file in this package maps to its original source path in the project.
Use this to place redesigned assets back into the correct location under `app/src/nitcwiki/res/`.

## Assets

| Package File | Source Path |
|-------------|-------------|
| animated_splash.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/animated_splash.xml` |
| feed_header_wordmark.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/feed_header_wordmark.xml` |
| ic_w_logo_circle.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/ic_w_logo_circle.xml` |
| ic_w_logo_shadow.png | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/ic_w_logo_shadow.png` |
| ic_wikipedia_b.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/ic_wikipedia_b.xml` |
| ic_wikipedia_w.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/ic_wikipedia_w.xml` |
| launcher_adaptive.xml | `app/src/main/res/mipmap-anydpi-v26/launcher.xml` |
| launcher_background.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/launcher_background.xml` |
| launcher_foreground.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/launcher_foreground.xml` |
| launcher_hdpi.png | `app/src/main/res/mipmap-hdpi/launcher.png` |
| launcher_mdpi.png | `app/src/main/res/mipmap-mdpi/launcher.png` |
| launcher_xhdpi.png | `app/src/main/res/mipmap-xhdpi/launcher.png` |
| launcher_xxhdpi.png | `app/src/main/res/mipmap-xxhdpi/launcher.png` |
| launcher_xxxhdpi.png | `app/src/main/res/mipmap-xxxhdpi/launcher.png` |
| splash_background_drawable.xml | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/splash_background_drawable.xml` |
| w_nav_mark.png | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable-xxhdpi/w_nav_mark.png` |
| wp_wordmark.png | `/home/ajay/nitc-wiki-android/app/src/main/res/drawable/wp_wordmark.png` |

## Notes

- Source paths are relative to `app/` in the project root.
- To override for the nitcwiki flavor, place the new file at the corresponding path under `app/src/nitcwiki/res/`.
- If the source has density qualifiers (e.g. `mipmap-mdpi`), the nitcwiki override must use the same qualifiers.
