package com.example.myapplication.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.myapplication.R

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wv_web: WebView =findViewById(R.id.wv_web)
        //能够识别网页的脚本语言
        wv_web.settings.javaScriptEnabled=true
        wv_web.webViewClient=WebViewClient()
        wv_web.loadUrl("http://www.baidu.com")
    }
}
