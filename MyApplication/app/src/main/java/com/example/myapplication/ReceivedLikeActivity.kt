package com.example.myapplication

import android.content.Context
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.myapplication.adapter.AgreeNewFriendAdapter
import com.example.myapplication.adapter.ReceivedLikeAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.Group
import com.example.myapplication.model.Like

class ReceivedLikeActivity : AppCompatActivity() {

    private lateinit var lv_like: ListView
    val ReceivedLikelist = ArrayList<Like>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_received_like)


        lv_like = this.findViewById(R.id.lv_like)

        //定义数据源
        initData()

        //定义一个适配器
        val receivedLike_adapter = ReceivedLikeAdapter(this,R.layout.friend_like,ReceivedLikelist)
        //给控件加适配器
        lv_like.adapter=receivedLike_adapter

    }
    fun initData(){

        // val say_something="CREATE TABLE say_something (say_ID INTEGER PRIMARY KEY AUTOINCREMENT,triggered_at TIMESTAMP,"+
        //            "userId VARCHAR(50),friendAndMeId VARCHAR(50),message_content TEXT,imgURL TEXT);"

        //查询say_something表，根据userId查询say_ID和message_content
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        val query = """
            SELECT say_ID,message_content
            FROM say_something
            WHERE userId = '$currentUsername'
        """.trimIndent()
        val cursor: Cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val say_ID = cursor.getInt(cursor.getColumnIndex("say_ID"))
            val message_content = cursor.getString(cursor.getColumnIndex("message_content"))
            val query1 = """
            SELECT userID
            FROM LikeTable
            WHERE dynamicID = '$say_ID' and userID != '$currentUsername'
        """.trimIndent()
            val cursor1: Cursor = db.rawQuery(query1, null)
            while (cursor1.moveToNext()) {
                val userID = cursor1.getString(cursor1.getColumnIndex("userID"))
                ReceivedLikelist.add(
                    Like(
                        say_ID,userID,message_content
                    )
                )
            }
            cursor1.close()
        }
        cursor.close()
        db.close()
        //在查询say_something过程中，还要根据say_ID去查询LikeTable的dynamicID，查询谁点赞了，进而找出userID，并把userID和dynamicID封装起来。

    }
}
