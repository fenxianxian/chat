package com.example.myapplication.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


class FriendListAdapter(activity: Activity, val resId:Int, data:List<Friend>):ArrayAdapter<Friend>(activity,resId,data){

    val dbHelper= MySqliteHelper(activity,"chatHub",2)
    val db=dbHelper.writableDatabase

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val userImage:ImageView=myview.findViewById(R.id.user_photo)
        val user_nikeName:TextView=myview.findViewById(R.id.user_nikeName)
        //为每一个控件加载数据
        val friend:Friend? = getItem(position) //获取每一个控件的位置

        if(friend!=null) {
            userImage.setImageResource(friend.headPhoto)
            if(friend.friendRemarks == null){
                user_nikeName.text = friend.friendId
            }else{
                user_nikeName.text = friend.friendRemarks
            }


        }

        return myview
    }




}