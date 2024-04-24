package com.example.myapplication.add

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.NewFriendActivity
import com.example.myapplication.R
import com.example.myapplication.db.MySqliteHelper

class ApplyRemark : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_remark)

        val send_button: Button = findViewById(R.id.send_button)
        val apply_message: EditText = findViewById(R.id.apply_message)
        send_button.setOnClickListener{
            send_button.isEnabled = false  //设置按钮不可点击
            send_button.text="等待验证"
            send_button.setBackgroundColor(Color.parseColor("#D3D3D3"))
            val newFriend = intent.getStringExtra("userId")
            val current_username = intent.getStringExtra("friendId")
            val dbHelper= MySqliteHelper(this,"chatHub",2)
            val db=dbHelper.writableDatabase
            val values1 = ContentValues().apply {
                    put("userId",newFriend)
                    put("friendId",current_username)
                    put("agree",0)
                    put("apply_message",apply_message.text.toString())
                    put("is_interact",0)
                    put("friendType",0)
                    put("is_delete",0)
            }
            db.insert("user_friend",null,values1)

            Toast.makeText(this,"发送成功，等待对方验证!!!!", Toast.LENGTH_SHORT).show()
        }
    }
}
