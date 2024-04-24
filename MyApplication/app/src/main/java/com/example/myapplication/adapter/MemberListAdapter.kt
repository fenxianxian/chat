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
import com.example.myapplication.UserSpace
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.Group
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class MemberListAdapter(activity: Activity, val resId:Int, data:List<String>,val onlyId:Int,val userNichen:String?):ArrayAdapter<String>(activity,resId,data){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val user_nikeName:TextView=myview.findViewById(R.id.user_nikeName)
        val user_photo:ImageView=myview.findViewById(R.id.user_photo)
        val deleteUser:Button=myview.findViewById(R.id.deleteUser)
        val user: SharedPreferences = context.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var current_username = user.getString("username", "")

        //为每一个控件加载数据
        var item:String? = getItem(position)

        user_nikeName.text = item
        user_photo.setImageResource(R.drawable.touxian)

        deleteUser.setOnClickListener{
            val dbHelper = MySqliteHelper(context, "chatHub", 2)
            val db = dbHelper.writableDatabase
            val whereClause = "groupID = ? AND userID = ?" // 设置条件语句
            val whereArgs = arrayOf(onlyId.toString(), user_nikeName.text.toString()) // 设置条件值
            val deletedRows = db.delete("GroupMember", whereClause, whereArgs)

            val whereClause1 = "userId = ? AND friendId = ? AND onlyId=?" // 设置条件语句
            val whereArgs1 = arrayOf(user_nikeName.text.toString(), userNichen,onlyId.toString()) // 设置条件值
            val deletedRows1 = db.delete("user_friend", whereClause1, whereArgs1)

            if (deletedRows > 0 && deletedRows1 > 0) {
                val intent = Intent(context, UserSpace::class.java)
                intent.putExtra("userNichen",userNichen)
                intent.putExtra("friendType",1)
                intent.putExtra("onlyId",onlyId);
                parent.getContext().startActivity(intent)
                Toast.makeText(context,"踢出成功",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"踢出失败",Toast.LENGTH_SHORT).show()
            }
        }

        val dbHelper = MySqliteHelper(context, "chatHub", 2)
        val db = dbHelper.readableDatabase
        val selection = "groupID = ? AND isCreate = ?"
        val selectionArgs = arrayOf(onlyId.toString(),"1")
        val cursor = db.query("GroupMember", arrayOf("userID"), selection, selectionArgs, null, null, null)
        while (cursor.moveToNext()) {
            val userID = cursor.getString(cursor.getColumnIndex("userID"))
            if(current_username == userID){
                if(user_nikeName.text.toString() == userID){
                    deleteUser.visibility = View.GONE
                }
            }else{
                deleteUser.visibility = View.GONE
            }
        }

        cursor.close()
        db.close()

        return myview
    }

}