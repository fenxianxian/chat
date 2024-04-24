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
import com.example.myapplication.model.Like
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class ReceivedLikeAdapter(activity: Activity, val resId:Int, data:List<Like>):ArrayAdapter<Like>(activity,resId,data){

    val dbHelper= MySqliteHelper(activity,"chatHub",2)
    val db=dbHelper.writableDatabase



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val user_like:TextView=myview.findViewById(R.id.user_like)
        val you_dongtai:TextView=myview.findViewById(R.id.you_dongtai)

        //为每一个控件加载数据
        val friendLike:Like? = getItem(position) //获取每一个控件的位置
        user_like.text = friendLike?.userID
        you_dongtai.text = friendLike?.message_content

        return myview
    }




}