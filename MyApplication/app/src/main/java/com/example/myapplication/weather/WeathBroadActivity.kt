package com.example.myapplication.weather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.concurrent.thread

class WeathBroadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weath_broad)
        val iv_back:ImageView=findViewById(R.id.ib_back)
        iv_back.setOnClickListener{
//            val intent = Intent(this,HttpURLActivity::class.java)
//            startActivity(intent)
            finish()
        }
        val ib_weather:ImageView=findViewById(R.id.ib_weather)
        ib_weather.setOnClickListener {
            val edt_weather:EditText=findViewById(R.id.edt_city)
            val str_city = edt_weather.text.toString()
            if(str_city.isNotEmpty()){
                //发送请求
                SendRequestOkHttp(str_city)
            }else{
                edt_weather.setError("请输入地区名称")
            }
        }
    }

    private fun SendRequestOkHttp(city: String) {
        //要进行网络请求，必须开启一个子线程
        thread{
            //第一步创建一个客户端
            val client= OkHttpClient()
            //第二步创建一个访问请求的对象
            val request= Request.Builder().url("https://www.yiketianqi.com/free/day?appid=32739685&appsecret=X8ZuU1gR&unescape=1&city=$city").build()
            //第三步就要去读取数据并返回数据
            val reponse = client.newCall(request).execute()
            val reponsedata=reponse.body?.string()
            Log.d("Weather",reponsedata.toString())
            //第四步解析数据
            if(reponsedata!=null){
                jsondata(reponsedata)
            }else{
                Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
        }

    }
    //解析数据的方法
    private fun jsondata(res:String){
        val jsondata=JSONObject(res)
        val city=jsondata.optString("city")
        var data=""//是把所有的天气信息放入到data变量中，最后把data数据加载到空间上
        if(city.isNotEmpty()){
            val date=jsondata.optString("date")
            val weather=jsondata.optString("wea")
            val week=jsondata.optString("week")
            val newtem=jsondata.optString("tem")
            val hightem=jsondata.optString("tem_day")
            val lowtem=jsondata.optString("tem_night")
            val wind=jsondata.optString("win")
            val speed=jsondata.optString("win_speed")
            data="$date $week\n地区：$city\n 天气：$weather\n 现在的温度：$newtem\n 今天的温度:"+
                    "$lowtem~$hightem\n 风向：$wind\n 风力：$speed"
        }else{
            data=""
        }
        showdata(data)
    }
    private fun showdata(data:String){
        //把解析数据展示在控件上，更新控件的时候要在子进程进行
        val tv_weather: TextView = findViewById(R.id.tv_weather)
        runOnUiThread{
            if (data.isNotEmpty()){
                tv_weather.setText(data)
            }else{
                tv_weather.setText("查无该地区的天气信息\n请检查查询信息是否有误！")
            }
        }
    }
}
