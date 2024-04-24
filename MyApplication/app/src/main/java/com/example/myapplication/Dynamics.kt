package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.example.myapplication.adapter.DynamicsAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.PostingUpdate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Dynamics : AppCompatActivity() {

    private lateinit var lv_user: ListView
    val DynamicsList=ArrayList<PostingUpdate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic)
        lv_user = findViewById(R.id.lv_user)

//        //定义数据源
        initData()

        //定义一个适配器
        val PostingUpdateAdapter = DynamicsAdapter(this,R.layout.fragment_second,DynamicsList)
        //给控件加适配器
        lv_user.adapter=PostingUpdateAdapter



    }
    fun initData(){
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        var friend_name = intent.getStringExtra("friend_name")
        var nichen = intent.getStringExtra("nichen")
        val selection = "userId = ?  ORDER BY triggered_at DESC"
        val selectionArgs: Array<String?>
        //Toast.makeText(this,friend_name,Toast.LENGTH_SHORT).show()

        selectionArgs = arrayOf(nichen)


        val cursor = db.query("say_something",null,selection,selectionArgs,null,null,null)
        if(cursor.moveToFirst())
        {
            do{
                val say_id = cursor.getLong(cursor.getColumnIndex("say_ID"))
                val time = cursor.getLong(cursor.getColumnIndex("triggered_at"))
                // 创建一个 Date 对象，将时间戳转换为日期对象
                val date = Date(time)
                // 创建一个 SimpleDateFormat 对象，定义输出的日期格式
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                // 将 Date 对象格式化为年月日格式的字符串
                val formattedDate = dateFormat.format(date)

                val content = cursor.getString(cursor.getColumnIndex("message_content"))
                val imgURL = cursor.getString(cursor.getColumnIndex("imgURL"))
                DynamicsList.add(
                       if(friend_name==null){
                           PostingUpdate(
                               say_id,time,formattedDate,nichen.toString(),content,R.drawable.xiaoai,imgURL
                           )
                       }else{
                           PostingUpdate(
                               say_id,time,formattedDate,friend_name.toString(),content,R.drawable.xiaoai,imgURL
                           )
                       }

                );
            }while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }




}
