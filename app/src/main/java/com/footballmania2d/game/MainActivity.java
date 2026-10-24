package com.footballmania2d.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import java.util.Locale;

public class MainActivity extends Activity {

    private WebView web;
    private Object backCallback; // OnBackInvokedCallback on API 33+
    private TextToSpeech tts;
    private volatile boolean ttsReady = false;
    private String pendingText = null, pendingLang = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        web.setBackgroundColor(0xFF070B14);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }
        });

        // Prefer Google's speech engine (best Arabic voices); Android falls back to the default engine if it is missing.
        tts = new TextToSpeech(this, status -> {
            ttsReady = (status == TextToSpeech.SUCCESS);
            if (ttsReady && pendingText != null) runOnUiThread(() -> { doSpeak(pendingText, pendingLang); pendingText = null; });
        }, "com.google.android.tts");
        web.addJavascriptInterface(new NativeBridge(), "FMNative");
        setContentView(web);
        hideSystemUi();

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl("file:///android_asset/index.html");

        if (Build.VERSION.SDK_INT >= 33) {
            OnBackInvokedCallback cb = this::handleBack;
            backCallback = cb;
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, cb);
        }
    }

    /** Small bridge used by the game (window.FMNative) for haptic feedback. */
    private class NativeBridge {
        @JavascriptInterface
        public void vibrate(int ms) { doVibrate(Math.max(5, Math.min(ms, 1000))); }

        @JavascriptInterface
        public void speak(final String text, final String lang) { runOnUiThread(() -> doSpeak(text, lang)); }

        @JavascriptInterface
        public void stopSpeak() { runOnUiThread(() -> { if (tts != null) tts.stop(); }); }

        /** "ok", "missing", "pending" or "none" */
        @JavascriptInterface
        public String ttsStatus(String lang) {
            if (tts == null) return "none";
            if (!ttsReady) return "pending";
            try {
                for (String tag : candidates(lang))
                    if (tts.isLanguageAvailable(Locale.forLanguageTag(tag)) >= TextToSpeech.LANG_AVAILABLE) return "ok";
                return "missing";
            } catch (Exception e) { return "missing"; }
        }

        @JavascriptInterface
        public void installVoice() {
            runOnUiThread(() -> {
                try { startActivity(new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); }
                catch (Exception e) {
                    try { startActivity(new Intent("com.android.settings.TTS_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); } catch (Exception ignored) { }
                }
            });
        }
    }

    /** Voices to try for a language, most specific first. */
    private static String[] candidates(String lang) {
        if (lang != null && lang.startsWith("ar"))
            return new String[]{"ar-SA", "ar-AE", "ar-EG", "ar-XA", "ar"};
        return new String[]{"en-GB", "en-US", "en-AU", "en-IN", "en"};
    }

    private boolean useLanguage(String lang) {
        for (String tag : candidates(lang)) {
            try {
                Locale loc = Locale.forLanguageTag(tag);
                if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
                    int r = tts.setLanguage(loc);
                    if (r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED) return true;
                }
            } catch (Exception ignored) { }
        }
        return false;
    }

    private void doSpeak(String text, String lang) {
        if (tts == null || text == null) return;
        if (!ttsReady) { pendingText = text; pendingLang = lang; return; }
        try {
            boolean ar = lang != null && lang.startsWith("ar");
            if (!useLanguage(lang)) {
                // Arabic voice not installed: stay silent rather than reading Arabic with an English voice.
                if (ar) return;
                try { tts.setLanguage(Locale.getDefault()); } catch (Exception ignored) { }
            }
            tts.setSpeechRate(ar ? 1.0f : 1.08f);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fm");
        } catch (Exception ignored) { }
    }

    @SuppressWarnings("deprecation")
    private void doVibrate(int ms) {
        try {
            Vibrator v;
            if (Build.VERSION.SDK_INT >= 31) {
                VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                v = vm != null ? vm.getDefaultVibrator() : null;
            } else {
                v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (v == null || !v.hasVibrator()) return;
            if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            else v.vibrate(ms);
        } catch (Exception ignored) { }
    }

    /** Ask the game to handle Back (close a dialog, leave a screen...). If it can't, exit. */
    private void handleBack() {
        if (web == null) { finish(); return; }
        web.evaluateJavascript(
                "(window.fmBack && window.fmBack()) ? '1' : '0'",
                value -> { if (!"\"1\"".equals(value)) finish(); });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Only used on Android 12L and older; newer versions use OnBackInvokedCallback.
        handleBack();
    }

    @SuppressWarnings("deprecation")
    private void hideSystemUi() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
            WindowInsetsController c = w.getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.systemBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            w.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) tts.stop();
        if (web != null) { web.onPause(); web.pauseTimers(); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) { web.onResume(); web.resumeTimers(); }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (web != null) web.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= 33 && backCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback((OnBackInvokedCallback) backCallback);
        }
        if (tts != null) { tts.stop(); tts.shutdown(); tts = null; }
        if (web != null) { web.destroy(); web = null; }
        super.onDestroy();
    }
}
