package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import com.example.myapplication.adapter.AgreeNewFriendAdapter
import com.example.myapplication.adapter.FriendListAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend

class HasNewFriendActivity : AppCompatActivity() {

    private lateinit var lv_user: ListView
    val friendlist=ArrayList<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_has_new_friend)

        lv_user = this.findViewById(R.id.lv_user)

        //定义数据源
        initData()

        //定义一个适配器
        val friend_adapter = AgreeNewFriendAdapter(this,R.layout.agree_new_friend,friendlist)
        //给控件加适配器
        lv_user.adapter=friend_adapter

        val toolbar_back =this.findViewById<LinearLayout>(R.id.toolbar_back)
        toolbar_back.setOnClickListener{
                    finish()
                    val requestCode = intent.getIntExtra("requestCode", -1)
                    if (requestCode == 1) {
                        val intent = Intent(this,MainActivity::class.java)
                        intent.putExtra("jump_who","please jump MessageFragment");
                        startActivity(intent)
                    } else if(requestCode == 2){
                        val intent = Intent(this,NewFriendActivity::class.java)
                        intent.putExtra("jump_who","please jump MessageFragment");
                        startActivity(intent)
                    }

        }



    }
    fun initData() {
        val dbHelper= MySqliteHelper(this,"chatHub",2)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user_friend",null,null,null,null,null,null)

        val userPrefs = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        if(cursor.moveToFirst())
        {
            do{
                val id = cursor.getColumnIndex("id")
                val userName = cursor.getString(cursor.getColumnIndex("userId"))
                if(currentUsername==userName){
                    val friendName = cursor.getString(cursor.getColumnIndex("friendId"))
                    val agree = cursor.getInt(cursor.getColumnIndex("agree"))
                    val apply_message = cursor.getString(cursor.getColumnIndex("apply_message"))
                    val friendType = cursor.getInt(cursor.getColumnIndex("friendType"))
                    val onlyId = cursor.getInt(cursor.getColumnIndex("onlyId"))
                    val friendRemarks = cursor.getString(cursor.getColumnIndex("friendRemarks"))
                    val is_delete = cursor.getInt(cursor.getColumnIndex("is_delete"))
                    //Toast.makeText(this,agree.toString(), Toast.LENGTH_SHORT).show()
                    if(agree==0){
                        friendlist.add(
                            Friend(
                                id,userName,friendName,apply_message,R.drawable.xiaoai,friendType,onlyId,friendRemarks,is_delete,false
                            )
                        );
                    }
                }
            }while (cursor.moveToNext())
        }
        cursor.close()
    }


}
