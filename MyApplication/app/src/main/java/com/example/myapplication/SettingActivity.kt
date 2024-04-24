package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.exit.ActivityBox
import com.example.myapplication.exit.BaseActivity

class SettingActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

//        var forceOffline = findViewById<Button>(R.id.forceOffline)
//        forceOffline.setOnClickListener {
//           val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
//            sendBroadcast(intent)
//        }

        var toolbar_back = findViewById<LinearLayout>(R.id.toolbar_back)
        toolbar_back.setOnClickListener {
           finish()
        }
    }

    //dailog具有交互功能
    fun onClickCancle(view: View){
        //构建在当前activity
        AlertDialog.Builder(this).apply {
            setTitle("确认对话框")
            setMessage("您确定要退出APP吗")
            setPositiveButton("取消"){ dailog: DialogInterface, which:Int->1}//1表示退出，还在当前页面中
            setNegativeButton("退出"){dailog,which:Int-> ActivityBox.finishAll()}
            show()
        }
    }
}
