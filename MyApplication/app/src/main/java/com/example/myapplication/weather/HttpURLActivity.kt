package com.example.myapplication.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class HttpURLActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_url)
        val btn_sendRequest:Button=findViewById(R.id.btn_sendRequest)
        btn_sendRequest.setOnClickListener{
            sendRequestwithHttpURLConetion()
        }
    }
    private fun sendRequestwithHttpURLConetion(){
        //开启子线程来进行网络请求
        thread {
            var connection:HttpURLConnection?=null        //定义一个链接的对象

            //定义一个URL资源
            val url=URL("http://www.baidu.com")
            //发送请求的方式
            connection=url.openConnection()as HttpURLConnection
            connection.connectTimeout=8000
            connection.readTimeout=8000
            //定义一个读数据的过程
            val input:InputStream=connection.inputStream
            val inputStreamReader=InputStreamReader(input)
            val reader=BufferedReader(inputStreamReader)
            //定义一个容器来接收从网页读取到的数据
            val response=StringBuffer()
            reader.use{
                reader.forEachLine {
                    response.append(it)
                }
            }
            showReponse(response.toString())
        }
    }
    private fun showReponse(reponse:String){
        runOnUiThread {
            val tv_show:TextView=findViewById(R.id.tv_text)
            tv_show.setText(reponse)
        }
    }
}
