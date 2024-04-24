package com.example.myapplication.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.model.User

import org.w3c.dom.Text

class UserAdapter(activity: Activity, val resId:Int, data:List<User>):ArrayAdapter<User>(activity,resId,data){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val userImage:ImageView=myview.findViewById(R.id.user_photo)
        val user_nikeName:TextView=myview.findViewById(R.id.user_nikeName)
        val user_message:TextView = myview.findViewById(R.id.user_message)
        //为每一个控件加载数据
        val user:User? = getItem(position) //获取每一个控件的位置

        if(user!=null)
        {
            userImage.setImageResource(user.headPhote)
            user_nikeName.text = user.name
            user_message.text = ""
        }

        return myview
    }
}