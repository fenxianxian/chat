package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.myapplication.db.MySqliteHelper

class DeleteUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        var deleteUser = findViewById<Button>(R.id.deleteUser)

        var friend_name = intent.getStringExtra("friend_name")
        var nichen = intent.getStringExtra("nichen")
        var friendType = intent.getStringExtra("friendType")

        deleteUser.setOnClickListener{
            val dbHelper = MySqliteHelper(this, "chatHub", 2)
            val db = dbHelper.writableDatabase
            val user: SharedPreferences = this.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
            var current_username = user.getString("username", "")
            val selection = "userId = ? AND friendId = ?  AND friendType = ?"
            val selectionArgs1 = arrayOf(current_username,nichen,"0")
           // val selectionArgs2 = arrayOf(nichen,current_username,"0")
            val values1 = ContentValues().apply {
                put("is_delete", 1)
            }
            val whereClause = "userId = ? AND friendId = ?" // 设置条件语句
            val whereArgs = arrayOf(nichen, current_username) // 设置条件值
            val rowsAffected = db.update(
                "user_friend",
                values1,
                whereClause,
                whereArgs
            )
            val deletedRows = db.delete("user_friend", selection, selectionArgs1)

            val cursor = db.query("chat_history",null,null,null,null,null,null)
            if(cursor.moveToFirst()) {
                do {
                    val sender = cursor.getString(cursor.getColumnIndex("sender"))
                    val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                    if (current_username == sender) {
                        // val receiver = cursor.getString(cursor.getColumnIndex("receiver"))
                        if (nichen == receiver) {
                            //val s_show =cursor.getInt(cursor.getColumnIndex("s_show"))
                            //把s_show置为1
                            val values = ContentValues()
                            values.put("s_show", 1)
                            val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
                            val whereArgs = arrayOf(current_username, nichen) // 设置条件值
                            db.update(
                                "chat_history",
                                values,
                                whereClause,
                                whereArgs
                            )
                        }
                    }
                    if (current_username == receiver) {
                        if (nichen == sender) {
                            //val r_show =cursor.getInt(cursor.getColumnIndex("r_show"))
                            //把r_show置为1
                            val values = ContentValues()
                            values.put("r_show", 1)
                            val whereClause = "sender = ? AND receiver = ?" // 设置条件语句
                            val whereArgs = arrayOf(nichen, current_username) // 设置条件值
                            db.update(
                                "chat_history",
                                values,
                                whereClause,
                                whereArgs
                            )
                        }
                    }
                } while (cursor.moveToNext())
            }
            if (deletedRows > 0 && rowsAffected > 0) {
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("jump_who","please jump MessageFragment");
                startActivity(intent)
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"删除失败",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
