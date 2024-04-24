package com.example.myapplication.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.chat.Msg
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class SelectFriendsAdapter(activity: Activity, val resId:Int, data:List<Friend>,val determineButton: Button):ArrayAdapter<Friend>(activity,resId,data){

    val dbHelper= MySqliteHelper(activity,"chatHub",2)
    val db=dbHelper.writableDatabase
    private val selectList = ArrayList<String>()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview:View
        //为视图添加布局文件
        myview=LayoutInflater.from(context).inflate(resId,parent,false)
        //为框架添加具体的控件
        val userImage:ImageView=myview.findViewById(R.id.user_picture)
        val user_nikeName:TextView=myview.findViewById(R.id.user_name)
        val checkbox:CheckBox=myview.findViewById(R.id.checkbox)

        //为每一个控件加载数据
        val friend:Friend? = getItem(position) //获取每一个控件的位置

        if(friend!=null) {
            userImage.setImageResource(friend.headPhoto)
            user_nikeName.text = friend.friendId
        }
        // 设置多选框状态变化监听器
        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectList.add("${friend?.friendId}");
                determineButton.setBackgroundColor(Color.parseColor("#FFA500"))
               // Toast.makeText(context, "选中了 ${friend?.friendId}", Toast.LENGTH_SHORT).show()
            } else {
                selectList.remove("${friend?.friendId}");
                if(selectList.size==0){
                    determineButton.setBackgroundColor(Color.parseColor("#D3D3D3"))
                }
               // Toast.makeText(context, "取消选中 ${friend?.friendId}", Toast.LENGTH_SHORT).show()
            }
        }
        return myview
    }

    // 获取 selectList 长度的方法
    fun getSelectListSize(): Int {
        return selectList.size
    }

    fun getSelectList(): ArrayList<String> {
        return selectList
    }



}