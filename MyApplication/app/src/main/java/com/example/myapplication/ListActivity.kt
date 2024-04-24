package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.adapter.UserAdapter
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.User
import kotlinx.android.synthetic.main.user_list.*


//该类暂时没用~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  详细请阅读FriendFragment，包括activity_list.xml也没用
class ListActivity : AppCompatActivity() {

    val userlist=ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        //初始化控件
        val lv_user: ListView = findViewById(R.id.lv_user)
        //定义数据源
        initData()
        //定义一个适配器
        val user_adapter = UserAdapter(this,R.layout.user_list,userlist)
        //给控件加适配器
        lv_user.adapter=user_adapter
    }
    fun initData() {
        val dbHelper= MySqliteHelper(this,"chatHub",1)
        val db=dbHelper.writableDatabase
        val cursor = db.query("user",null,null,null,null,null,null)
        if(cursor.moveToFirst())
        {
            do{
                val id = cursor.getColumnIndex("id")
                val username = cursor.getString(cursor.getColumnIndex("username"))
                val nikeName = cursor.getString(cursor.getColumnIndex("nickname"))
                val password = cursor.getString(cursor.getColumnIndex("password"))
                val sex = cursor.getString(cursor.getColumnIndex("sex"))
                val address = cursor.getString(cursor.getColumnIndex("address"))
                //val headPhoto = cursor.getColumnIndex("headPhoto")
                val signature = cursor.getString(cursor.getColumnIndex("signature"))
                val regDate = cursor.getString(cursor.getColumnIndex("regDate"))
                userlist.add(
                    User(
                           id,username,nikeName,password,"",sex,address,R.drawable.xiaoai , signature,regDate
                    )
                );
            }while (cursor.moveToNext())
        }
        cursor.close()
    }
}
