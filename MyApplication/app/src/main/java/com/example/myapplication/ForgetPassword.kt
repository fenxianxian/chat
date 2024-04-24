package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar


class ForgetPassword : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

    }

//    override  fun onKeyDown( keyCode:Int,  event:KeyEvent):Boolean {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            moveTaskToBack(true);
//            Toast.makeText(this,"55", Toast.LENGTH_SHORT).show()
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
