package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.chat.Msg
import com.example.myapplication.chat.MsgAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {

    private val msgList = ArrayList<Msg>()

    private lateinit var adapter: MsgAdapter
    var onlyId=-1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(msgList)
        }
        recyclerView.adapter = adapter
        val friend_name = intent.getStringExtra("friend_name")
        val nichen = intent.getStringExtra("nichen")
        val friend_type = intent.getIntExtra("friend_type",-1)
        onlyId = intent.getIntExtra("onlyId",-1)
        //onlyId= intent.getStringExtra("onlyId")

        val friendName: TextView = findViewById(R.id.friend_name)
        var is_delete = intent.getIntExtra("is_delete",-1)

//        if(is_delete==0){
            initMsg(nichen,friend_type,onlyId,friend_name)
       // }

       // 滚动到底部，查看最新消息，要放在 initMsg(friend_name)后面
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        //Toast.makeText(this,onlyId.toString(),Toast.LENGTH_SHORT).show()
        if(friend_type==0){
            if(friend_name==null){
                friendName.setText(nichen)
            }else{
                friendName.setText(friend_name+"(昵称："+nichen+")")
            }
        }else if(friend_type==1){
            val dbHelper= MySqliteHelper(this,"chatHub",2)
            val db=dbHelper.writableDatabase
            val query = """
            SELECT count(userID) 
            FROM GroupMember 
            where groupID = '${onlyId}'
        """.trimIndent()
            var count = 0
            val cursor: Cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            //Toast.makeText(this,count.toString(),Toast.LENGTH_SHORT).show()
            cursor.close()
            db.close()
            if(friend_name==null){
                friendName.setText(nichen+"("+count.toString()+")")
            }else{
                friendName.setText(friend_name+"("+count.toString()+")")
            }
        }
        send.setOnClickListener{ v ->
            var is_delete = intent.getIntExtra("is_delete",-1)
            if(is_delete==1){
                Toast.makeText(this,"对方以把你删除，你无法发消息",Toast.LENGTH_SHORT).show()
            }else if(is_delete==0){
                doSend(v,nichen)
            }

        }

        val toolbar_back =findViewById<LinearLayout>(R.id.toolbar_back)
        toolbar_back.setOnClickListener{

            val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
            val currentUsername = userPrefs.getString("username", "")
                    val dbHelper= MySqliteHelper(this,"chatHub",2)
                    val db1=dbHelper.writableDatabase
                    val cursor1 = db1.query("chat_history",null,null,null,null,null,null)
                    if(cursor1.moveToFirst()) {
                        do {
                            val receiver = cursor1.getString(cursor1.getColumnIndex("receiver"))
                            if(friend_type==0){
                                //说明是单聊
                                if (currentUsername == receiver) {
                                    val sender = cursor1.getString(cursor1.getColumnIndex("sender"))
                                    if(sender == nichen){
                                        var letter = cursor1.getInt(cursor1.getColumnIndex("letter"))
                                        if (letter==1){
                                            val values = ContentValues()
                                            values.put("letter", 0)

                                            val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
                                            val whereArgs = arrayOf(nichen, currentUsername) // 设置条件值

                                            db1.update(
                                                "chat_history",
                                                values,
                                                whereClause,
                                                whereArgs
                                            )
//                                      Toast.makeText(this, sender, Toast.LENGTH_SHORT).show()
//                                        Toast.makeText(this, receiver, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }else if(friend_type==1)
                            {
                                //Toast.makeText(this,(friend_name==receiver).toString(), Toast.LENGTH_SHORT).show()
                                //Toast.makeText(this,, Toast.LENGTH_SHORT).show()
                                //说明是群聊
                                if (nichen == receiver) {
                                    val sender = cursor1.getString(cursor1.getColumnIndex("sender"))
                                    if (sender != currentUsername) {

                                        val only_Id = cursor1.getInt(cursor1.getColumnIndex("onlyId"))
                                        if(onlyId == only_Id){
                                            var letter =
                                                cursor1.getInt(cursor1.getColumnIndex("letter"))
                                            if (letter == 1) {
                                                val values = ContentValues()
                                                values.put("letter", 0)

                                                val whereClause =
                                                    "sender = ? AND receiver = ? AND onlyId = ?" // 设置条件语句
                                                val whereArgs =
                                                    arrayOf(sender, nichen,only_Id.toString()) // 设置条件值

                                                db1.update(
                                                    "chat_history",
                                                    values,
                                                    whereClause,
                                                    whereArgs
                                                )
//                                      Toast.makeText(this, sender, Toast.LENGTH_SHORT).show()
//                                        Toast.makeText(this, receiver, Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    }
                                }
                            }

                        }while (cursor1.moveToNext())
                    }
                    cursor1.close()

            val requestCode = intent.getIntExtra("requestCode", -1)
            if (requestCode == 1) {
                finish()
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("jump_who","please jump MessageFragment");
                startActivity(intent)
            } else if(requestCode == 2){
                finish()
                val intent = Intent(this,NewFriendActivity::class.java)
                intent.putExtra("jump_who","please jump MessageFragment");
                startActivity(intent)
            }else if(requestCode == 3){
                finish()
                val intent = Intent(this,GroupChat::class.java)
                startActivity(intent)
            }else if(requestCode == 4){
                finish()
                val intent = Intent(this,UserSpace::class.java)
                intent.putExtra("friendType", 0)
                intent.putExtra("userName", friend_name)
                intent.putExtra("userNichen",nichen)
                intent.putExtra("onlyId", onlyId)
                intent.putExtra("is_delete", is_delete)

                startActivity(intent)
            }
        }
    }
    var interact=false;
    fun doSend(v: View?,friendName: String?) {
        when (v) {
            send -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    var current_username = user.getString("username", "")

                    val currentTimestamp =System.currentTimeMillis()

                    val dbHelper= MySqliteHelper(this,"chatHub",2)
                    val db=dbHelper.writableDatabase
//                    val values1 = ContentValues()
//                    values1.put("letter", 0) //其它都不是最新消息
//                    val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
//                    val whereArgs = arrayOf(current_username, friendName) // 设置条件值
//                    db.update(
//                        "chat_history",
//                        values1,
//                        whereClause,
//                        whereArgs
//                    )
                    val values = ContentValues().apply {
                        put("triggered_at",currentTimestamp)
                        put("sender",current_username)
                        put("receiver",friendName)
                        put("message_type","")
                        put("message_content",content)
                        put("session_id",0)
                        put("letter",1)
                        put("s_show",0);
                        put("r_show",0);
                        put("onlyId",onlyId)
                    }
                    db.insert("chat_history",null,values)

                    val db1=dbHelper.writableDatabase
                    val cursor1 = db1.query("user_friend",null,null,null,null,null,null)
                    if(cursor1.moveToFirst()) {
                        do {
                            val userId = cursor1.getString(cursor1.getColumnIndex("userId"))
                            if (current_username == userId) {
                                val friendId = cursor1.getString(cursor1.getColumnIndex("friendId"))
                                if(friendId == friendName){
                                    var is_interact = cursor1.getInt(cursor1.getColumnIndex("is_interact"))
                                    if(is_interact==1){
                                        interact = true
                                        break
                                    }
                                }
                            }
                        }while (cursor1.moveToNext())
                    }
                    cursor1.close()
                    if(!interact){
                        val values5 = ContentValues()
                        values5.put("is_interact", 1)
                        val whereClause = "userId = ? AND friendId = ?" // 设置条件语句
                        val whereArgs = arrayOf(current_username, friendName) // 设置条件值
                        db1.update(
                            "user_friend",
                            values5,
                            whereClause,
                            whereArgs
                        )
                        val values1 = ContentValues()
                        values1.put("is_interact", 1)
                        val whereClause1 = "userId = ? AND friendId = ?" // 设置条件语句
                        val whereArgs1 = arrayOf(friendName, current_username) // 设置条件值
                        db1.update(
                            "user_friend",
                            values1,
                            whereClause1,
                            whereArgs1
                        )
                    }
                    val msg = Msg(content, Msg.TYPE_SENT,current_username)
                    msgList.add(msg)
                    adapter.notifyItemInserted(msgList.size - 1) // 当有新消息时，刷新RecyclerView中的显示
                    recyclerView.scrollToPosition(msgList.size - 1)  // 将
                    //RecyclerView定位到最后一行
                    inputText.setText("") // 清空输入框中的内容
                }
            }
        }
    }
//    override fun onClick(v: View?) {
//
//    }

    private fun initMsg(nichen: String?,friendType:Int,onlyID:Int,friend_name:String?) {
//        val msg1 = Msg("Hello guy.", Msg.TYPE_RECEIVED)
//        msgList.add(msg1)
//        val msg2 = Msg("Hello. Who is that?", Msg.TYPE_SENT)
//        msgList.add(msg2)
//        val msg3 = Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED)
//        msgList.add(msg3)

        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("chat_history",null,null,null,null,null,null)
        val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")

        if(cursor.moveToFirst())
        {
            do{
               // val triggeredAt = cursor.getColumnIndex("triggered_at")
                val sender = cursor.getString(cursor.getColumnIndex("sender"))
                val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                val only_Id = cursor.getInt(cursor.getColumnIndex("onlyId"))
                if(current_username==sender){
                   // val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                    if(nichen==receiver && onlyID == only_Id){
                        val myMessage = cursor.getString(cursor.getColumnIndex("message_content"))
                        val s_show =cursor.getInt(cursor.getColumnIndex("s_show"))
                        if(s_show==0){
                            val msg2 = Msg(myMessage, Msg.TYPE_SENT,current_username)
                            msgList.add(msg2)
                        }
                    }
                }
                if(friendType==0){
                    //说明不是群
                    if(current_username==receiver){
                        if(nichen==sender){
                            val myMessage = cursor.getString(cursor.getColumnIndex("message_content"))
                            val r_show =cursor.getInt(cursor.getColumnIndex("r_show"))
                            if(r_show==0){
                                var msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,"");
                                if(friend_name == null){
                                     msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,nichen)
                                }else{
                                     msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,friend_name)
                                }
                                msgList.add(msg2)
                            }
                        }
                    }
                }else if(friendType==1){
                    //说明是群
                    if(nichen==receiver){
                        if(sender!=current_username && onlyID == only_Id){
                            val myMessage = cursor.getString(cursor.getColumnIndex("message_content"))
                            val r_show =cursor.getInt(cursor.getColumnIndex("r_show"))
                            if(r_show==0){
                                //通过sender，和当前用户 找到备注
                                val dbHelper= MySqliteHelper(this,"chatHub",2)
                                val db=dbHelper.writableDatabase
                                val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                                var current_username = user.getString("username", "")
                                val selection = "userId = ? AND  friendId = ?"
                                val selectionArgs = arrayOf(current_username, sender)
                                val columns = arrayOf("friendRemarks")
                                val cursor = db.query("user_friend", columns, selection, selectionArgs, null, null, null)
                                var friendRemarks: String? = null
                                if (cursor.moveToFirst()) {
                                    friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                                }
                                //再判断备注是否为null，如果为null，走msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,sender)
                                var msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,"")
                                if(friendRemarks == null){
                                    msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,sender)
                                }else{
                                    msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,friendRemarks)
                                }
                                //如果不为null 走msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,备注)
//                                var msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,sender)
//                                if(sender == null){
//                                    msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,sender)
//                                }else{
//                                    msg2 = Msg(myMessage, Msg.TYPE_RECEIVED,friend_name)
//                                }
                                msgList.add(msg2)
                            }
                        }
                    }
                }

            }while (cursor.moveToNext())
        }
        cursor.close()
    }

}
