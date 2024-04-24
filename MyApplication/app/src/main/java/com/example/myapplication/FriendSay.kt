package com.example.myapplication

import PostingUpdateAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import com.example.myapplication.adapter.FriendListAdapter

import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.PostingUpdate

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import java.sql.Timestamp
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import java.util.*
import kotlin.collections.ArrayList


class FriendSay : AppCompatActivity() {

    private lateinit var lv_user: ListView
    val postingUpdate=ArrayList<PostingUpdate>()
    val flag:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_say)

        lv_user = findViewById(R.id.lv_user)

//        //定义数据源
        initData()


        //定义一个适配器
        val PostingUpdateAdapter = PostingUpdateAdapter(this,R.layout.fragment_second,postingUpdate)
        //给控件加适配器
        lv_user.adapter=PostingUpdateAdapter



    }

    fun initData() {
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        //val orderBy = "triggered_at DESC"
        val cursor = db.query("say_something",null,null,null,null,null,null)
        val cursor1 = db.query("user_friend",null,null,null,null,null,null)

        if(cursor1.moveToFirst())
        {
            do{
                val userName = cursor1.getString(cursor1.getColumnIndex("userId"))
                val friendName = cursor1.getString(cursor1.getColumnIndex("friendId"))
                val agree = cursor1.getInt(cursor1.getColumnIndex("agree"))
                val friendRemarks = cursor1.getString(cursor1.getColumnIndex("friendRemarks"))

                if(userName==currentUsername && agree == 1)
                {
                    if(cursor.moveToFirst())
                    {
                        do{
                            val userName1 = cursor.getString(cursor.getColumnIndex("userId"))
                           // Toast.makeText(this,friendName,Toast.LENGTH_SHORT).show()
                            if(friendName==userName1){
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
                                if(friendRemarks == null){
                                    postingUpdate.add(
                                        PostingUpdate(
                                            say_id,time,formattedDate,userName1,content,R.drawable.xiaoai,imgURL
                                        )
                                    );
                                }else{
                                    postingUpdate.add(
                                        PostingUpdate(
                                            say_id,time,formattedDate,friendRemarks,content,R.drawable.xiaoai,imgURL
                                        )
                                    );
                                }

                            }
                        }while (cursor.moveToNext())
                    }
                }
            }while (cursor1.moveToNext())

            // 对postingUpdate按照id属性进行排序
           // val sortedList = postingUpdate.sortedByDescending { it.id }

//            // 打印排序后的结果
//            sortedList.forEach { println(it) }
        }
        cursor.close()
        cursor1.close()
        postingUpdate.sortByDescending { it.id } //对应time
//        if(cursor.moveToFirst())
//        {
//            do{
//                val time = cursor.getLong(cursor.getColumnIndex("triggered_at"))
//                // 创建一个 Date 对象，将时间戳转换为日期对象
//                val date = Date(time)
//                // 创建一个 SimpleDateFormat 对象，定义输出的日期格式
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//                // 将 Date 对象格式化为年月日格式的字符串
//                val formattedDate = dateFormat.format(date)
//                val userName = cursor.getString(cursor.getColumnIndex("userId"))
//
//
//                if(currentUsername==userName){
//                    val friendAndMeId = cursor.getString(cursor.getColumnIndex("friendAndMeId"))
//                    val content = cursor.getString(cursor.getColumnIndex("message_content"))
//                        postingUpdate.add(
//                            PostingUpdate(
//                                formattedDate,userName,friendAndMeId,content,R.drawable.xiaoai
//                            )
//                        );
//                }
//            }while (cursor.moveToNext())
//        }
//        cursor.close()
    }
}
