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
import com.example.myapplication.model.Group
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class GroupListAdapter(activity: Activity, val resId:Int, data:List<Group>):ArrayAdapter<Group>(activity,resId,data){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val group_name:TextView=myview.findViewById(R.id.group_name)
        val group_Id:TextView=myview.findViewById(R.id.group_Id)

        //为每一个控件加载数据
        var item:Group? = getItem(position)

        group_name.text = item?.GroupName
        group_Id.text = item?.GroupId.toString()

        return myview
    }

}