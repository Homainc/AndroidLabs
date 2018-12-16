package com.homa_inc.androidlabs.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.homa_inc.androidlabs.Extensions.NewsWebViewClient
import com.homa_inc.androidlabs.R

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView = findViewById(R.id.webView)
        webView.webViewClient = NewsWebViewClient()
        webView.settings.javaScriptEnabled = true
        val url = WebViewActivityArgs.fromBundle(intent.extras).url
        webView.loadUrl(url)
    }
}