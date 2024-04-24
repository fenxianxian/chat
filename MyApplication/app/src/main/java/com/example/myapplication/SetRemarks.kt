package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.db.MySqliteHelper

class SetRemarks : AppCompatActivity(), TextWatcher{

    //为好友设置备注
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_remarks)
        var friend_name = intent.getStringExtra("friend_name")
        var friend_remark: EditText= findViewById<EditText>(R.id.friend_remark)
        friend_remark.setHint(friend_name)
        friend_remark.addTextChangedListener(this)

    }
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var friend_remark: EditText= findViewById<EditText>(R.id.friend_remark)
        val friend_remark_String:String = friend_remark.text.toString().trim()
        if(!friend_remark_String.isEmpty()){
            //Toast.makeText(this,"非空",Toast.LENGTH_SHORT).show()
            var determine: Button= findViewById<Button>(R.id.determine)
            determine.setBackgroundColor(Color.parseColor("#FFA500"))
            val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
            var current_username = user.getString("username", "")
           // var friend_name = intent.getStringExtra("friend_name")
            var nichen = intent.getStringExtra("nichen")
            determine.setOnClickListener{
                val dbHelper= MySqliteHelper(this,"chatHub",2)
                val db=dbHelper.writableDatabase
                // 执行更新操作
                val rowsAffected = db.update("user_friend", ContentValues().apply {
                    put("friendRemarks", friend_remark_String)
                }, "userId = ? AND friendId = ?", arrayOf(current_username, nichen))
                if (rowsAffected > 0) {
                    val intent = Intent(this,UserSpace::class.java)
                    intent.putExtra("friendType", 0)
                    intent.putExtra("userName", friend_remark_String)
                    intent.putExtra("userNichen",nichen)
                    intent.putExtra("onlyId", 0)
                    startActivity(intent)

                    Toast.makeText(this,"修改备注成功",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"修改备注失败",Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            var determine: Button= findViewById<Button>(R.id.determine)
            determine.setBackgroundColor(Color.parseColor("#D3D3D3"))
            //Toast.makeText(this,"空",Toast.LENGTH_SHORT).show()
        }
    }
}
