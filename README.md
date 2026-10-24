# Football Mania 2D (Android)

Offline disc-football game packaged as a full-screen Android WebView app.

- Game: `app/src/main/assets/index.html` (single self-contained file; edit and push to rebuild)
- Package: `com.footballmania2d.game` · minSdk 24 · targetSdk 36
- Build: GitHub Actions (`.github/workflows/build.yml`) produces a signed AAB + APK on every push to `main`
- Required repository secrets: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`
- Privacy policy: `docs/index.html` (serve with GitHub Pages from `/docs`)

دليل النشر خطوة بخطوة بالعربية: [GUIDE-AR.md](GUIDE-AR.md)
