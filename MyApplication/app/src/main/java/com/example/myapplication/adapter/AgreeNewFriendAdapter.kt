package com.example.myapplication.adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class AgreeNewFriendAdapter(activity: Activity, val resId:Int, data:List<Friend>):ArrayAdapter<Friend>(activity,resId,data){

    val dbHelper= MySqliteHelper(activity,"chatHub",2)
    val db=dbHelper.writableDatabase



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val userImage:ImageView=myview.findViewById(R.id.user_photo)
        val newFriend_nikeName:TextView=myview.findViewById(R.id.newFriend_nikeName)
        val apply_message:TextView=myview.findViewById(R.id.apply_message)
        //为每一个控件加载数据
        val friend:Friend? = getItem(position) //获取每一个控件的位置
        var flag:Boolean=false

        if(friend!=null) {
            userImage.setImageResource(friend.headPhoto)
            newFriend_nikeName.text = friend.friendId
            apply_message.text = friend.apply_message

        }
        val user: SharedPreferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")

        //点击同意按钮，表示愿意添加新好友
        val agreeRequest: Button = myview.findViewById(R.id.agreeRequest)
        val already_add:Button = myview.findViewById(R.id.already_add)
        agreeRequest.setOnClickListener{
            if(!flag){
                //Toast.makeText(context,friend?.friendId, Toast.LENGTH_SHORT).show()
                val values = ContentValues().apply {
                    put("userId",friend?.friendId)
                    put("friendId",current_username)
                    put("agree",1)
                    put("is_interact",0)
                    put("friendType",0)
                    put("is_delete",0)
                }
                db.insert("user_friend",null,values)

                val selection = "userId = ? AND friendId = ?  AND friendType = ? AND is_delete = ?"
                val selectionArgs1 = arrayOf(current_username,friend?.friendId,"0","1")
                db.delete("user_friend", selection, selectionArgs1)

                val selection2 = "userId = ? AND friendId = ?  AND friendType = ? AND is_delete = ?"
                val selectionArgs2 = arrayOf(friend?.friendId,current_username,"0","1")
                db.delete("user_friend", selection2, selectionArgs2)

                val cursor = db.query("user_friend",null,null,null,null,null,null)
                if(cursor.moveToFirst())
                {
                    do{
                        val userName = cursor.getString(cursor.getColumnIndex("userId"))
                        val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                        if(current_username==userName && friend?.friendId==friendName){
                            val values1 = ContentValues().apply {
                                put("agree", 1)
                            }
                            val whereClause = "userId = ? AND friendId = ?" // 设置条件语句
                            val whereArgs = arrayOf(userName, friendName) // 设置条件值
                            db.update(
                                "user_friend",
                                values1,
                                whereClause,
                                whereArgs
                            )
                        }
                    }while (cursor.moveToNext())
                }
                cursor.close()
                flag = true
            }
            agreeRequest.visibility = View.GONE
            already_add.visibility = View.VISIBLE
        }

        return myview
    }




}