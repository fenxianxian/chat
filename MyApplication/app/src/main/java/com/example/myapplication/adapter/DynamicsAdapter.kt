package com.example.myapplication.adapter

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import com.example.myapplication.model.PostingUpdate
import com.example.myapplication.model.User

import org.w3c.dom.Text
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.ofEpochMilli

import java.time.Instant
import java.util.*


class DynamicsAdapter(activity: Activity, val resId:Int, data:List<PostingUpdate>):ArrayAdapter<PostingUpdate>(activity,resId,data){

    val dbHelper = MySqliteHelper(activity, "chatHub", 2)
    val db = dbHelper.writableDatabase

    //核心方法，将数据源转为view
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myview: View
        if(convertView == null){
            myview = LayoutInflater.from(context).inflate(resId, parent, false)
        }else{
            myview = convertView
        }
        // 为框架添加具体的控件
        val userImage: ImageView = myview.findViewById(R.id.touxian)
        val user_nikeName: TextView = myview.findViewById(R.id.user_nikeName)
        val content: TextView = myview.findViewById(R.id.content)
        val send_date: TextView = myview.findViewById(R.id.send_date)
        val imgCamera: ImageView = myview.findViewById(R.id.imgCamera)
        val likes: ImageView = myview.findViewById(R.id.likes)
        val likesNumber: TextView = myview.findViewById(R.id.likesNumber)

        // 为每一个控件加载数据
        val postingUpdate: PostingUpdate? = getItem(position) // 获取每一个控件的位置
//        val postingUpdate1: PostingUpdate = data[position]
        if (postingUpdate != null) {
            userImage.setImageResource(postingUpdate.headPhoto)
            user_nikeName.text = postingUpdate.userId
            content.text = postingUpdate.content
            send_date.text = postingUpdate.triggered_at
            // 请求访问相册权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 1)
                }
            }
            if(postingUpdate.imgURL != ""){
                if(imgCamera!=null){
                    val newWidth = 200 // 新的宽度值
                    val newHeight = 200 // 新的高度值
                    val layoutParams = imgCamera.layoutParams
                    layoutParams.width = newWidth
                    layoutParams.height = newHeight
                    imgCamera.layoutParams = layoutParams
                }
            }else{
                if(imgCamera!=null){
                    val newWidth = 0 // 新的宽度值
                    val newHeight = 0 // 新的高度值
                    val layoutParams = imgCamera.layoutParams
                    layoutParams.width = newWidth
                    layoutParams.height = newHeight
                    imgCamera.layoutParams = layoutParams
                }
            }
            // 加载图片
            imgCamera.setImageURI(Uri.parse(postingUpdate.imgURL))
        }
        //初始化点赞数
        InitLikeNumber(postingUpdate,likesNumber)

        likes.setImageResource(R.drawable.nolike)
        InitRedLike(postingUpdate,likes)

        likes.setOnClickListener {
            val resourceId = likes.drawable?.constantState?.toString()
            val userPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val currentUsername = userPrefs.getString("username", "")
            val dbHelper= MySqliteHelper(context,"chatHub",2)
            val db=dbHelper.writableDatabase
            if (resourceId == ContextCompat.getDrawable(context, R.drawable.like)?.constantState?.toString()) {

                val selection = "dynamicID = ? AND userID = ?"
                val selectionArgs1 = arrayOf(postingUpdate?.say_id.toString(),currentUsername)
                val deletedRows = db.delete("LikeTable", selection, selectionArgs1)
                if(deletedRows > 0 ){
                    likes.setImageResource(R.drawable.nolike)
                    InitLikeNumber(postingUpdate,likesNumber)
                    Toast.makeText(context,"已取消点赞",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show()
                }
            }
            // 如果点击的是 like 图像
            else if (resourceId == ContextCompat.getDrawable(context, R.drawable.nolike)?.constantState?.toString()) {
                val values = ContentValues().apply {
                    put("dynamicID",postingUpdate?.say_id.toString())
                    put("userID",currentUsername)
                }
                var rowEffect = db.insert("LikeTable", null, values)
                if(rowEffect > 0 ){
                    likes.setImageResource(R.drawable.like)
                    InitLikeNumber(postingUpdate,likesNumber)
                    Toast.makeText(context,"点赞成功",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"点赞失败！！",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return myview
    }

    fun InitLikeNumber(postingUpdate:PostingUpdate?,likesNumber: TextView){

        val dbHelper= MySqliteHelper(context,"chatHub",2)
        val db=dbHelper.writableDatabase
        val selection = "dynamicID = ?"
        val selectionArgs = arrayOf(postingUpdate?.say_id.toString())
        val columns = arrayOf("COUNT(userID)")
        val cursor = db.query("LikeTable", columns, selection, selectionArgs, null, null, null)
        if(cursor != null && cursor.moveToFirst())
        {
            val count = cursor.getInt(0)
            likesNumber.text = count.toString()
        }
        cursor.close()
        db.close()
    }

    fun InitRedLike(postingUpdate:PostingUpdate?,likes: ImageView){
        val dbHelper= MySqliteHelper(context,"chatHub",2)
        val db=dbHelper.writableDatabase
        val userPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUsername = userPrefs.getString("username", "")
        val selection = "dynamicID = ? AND userID = ?"
        val selectionArgs = arrayOf(postingUpdate?.say_id.toString(),currentUsername)
        val cursor = db.query("LikeTable", null, selection, selectionArgs, null, null, null)
        if(cursor != null && cursor.moveToFirst())
        {
            likes.setImageResource(R.drawable.like)
        }
        cursor.close()
        db.close()
    }




}