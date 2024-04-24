package com.example.myapplication

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.db.MySqliteHelper
import kotlinx.android.synthetic.main.activity_posting_updates.*
import kotlinx.android.synthetic.main.fragment_second.*

class PostingUpdates : AppCompatActivity() {
    private val GALLERY_REQ_CODE = 1001
    private lateinit var imgCamera: ImageView
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_updates)

        val publishButton: Button = findViewById(R.id.publishButton)


        imgCamera = findViewById(R.id.imgCamera)
        val btnCamera = findViewById<Button>(R.id.btnCamera)

        btnCamera.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQ_CODE)
        }

        publishButton.setOnClickListener{
            val editText: EditText = findViewById(R.id.editText)
            val text = editText.text.toString()
            if (text.isNullOrEmpty()) {
                Toast.makeText(this,"不能为空", Toast.LENGTH_SHORT).show()
            } else {
               // 判断 ImageView 是否有设置图片 URI
                val hasImageURI = imgCamera.drawable != null

                val user: SharedPreferences = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                var current_username = user.getString("username", "")

                val currentTimestamp =System.currentTimeMillis()

                val dbHelper= MySqliteHelper(this,"chatHub",2)
                val db=dbHelper.writableDatabase

                if (hasImageURI) {
                    val values = ContentValues().apply {
                        put("triggered_at",currentTimestamp)
                        put("userId",current_username)
                        put("friendAndMeId",current_username)
                        put("message_content",text)
                        put("imgURL",selectedImageUri.toString())
                    }
                    db.insert("say_something",null,values)
                }else{
                    val values = ContentValues().apply {
                        put("triggered_at",currentTimestamp)
                        put("userId",current_username)
                        put("friendAndMeId",current_username)
                        put("message_content",text)
                        put("imgURL","")
                    }
                    db.insert("say_something",null,values)
                }

                finish()
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("jump_who","please jump UserFragment");
                startActivity(intent)
                Toast.makeText(this,"发表成功！！！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imgCamera.setImageURI(selectedImageUri)
        }
    }

}
