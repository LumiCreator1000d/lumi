package com.lumi.app

import android.app.Activity
import android.app.NotificationManager
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView

class RingActivity : Activity() {
    private var web: WebView? = null

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setShowWhenLocked(true); setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getSystemService(NotificationManager::class.java).cancel(42)

        val title = intent.getStringExtra("title") ?: "Bonjour"
        val label = intent.getStringExtra("label") ?: ""
        val music = intent.getStringExtra("music") ?: ""

        val w = WebView(this)
        web = w
        setContentView(w)
        w.settings.javaScriptEnabled = true
        w.settings.mediaPlaybackRequiresUserGesture = false
        w.addJavascriptInterface(object : Any() {
            @JavascriptInterface fun stop() { runOnUiThread { finish() } }
            @JavascriptInterface fun snooze() {
                Scheduler.one(this@RingActivity, System.currentTimeMillis() + 5 * 60_000L, 9999, "Encore un peu 💤", label, music)
                runOnUiThread { finish() }
            }
        }, "Android")
        w.loadUrl("file:///android_asset/ring.html?t=${Uri.encode(title)}&l=${Uri.encode(label)}&m=${Uri.encode(music)}")
    }

    override fun onDestroy() { web?.destroy(); super.onDestroy() }
}
