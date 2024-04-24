package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.db.MySqliteHelper
import com.example.myapplication.model.Friend
import java.security.MessageDigest
import java.security.SecureRandom

class Register : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)

        val registerBtn: Button = findViewById(R.id.register_button)
        registerBtn.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        var flag = false;
        if (validate(username, password, confirmPassword)) {

            val dbHelper= MySqliteHelper(this,"chatHub",2)
            val db=dbHelper.writableDatabase
            val cursor = db.query("user",null,null,null,null,null,null)
            if(cursor.moveToFirst())
            {
                do{
                    val name = cursor.getString(cursor.getColumnIndex("username"))
                    if(name==username){
                        flag=true;
                    }
                }while (cursor.moveToNext())
            }
            cursor.close()
            if(!flag){
                val saltLength = 16;
                val salt = generateSalt(saltLength);
                val hashedPassword = hashPassword(password, salt);
                val values = ContentValues().apply {
                    put("username",username)
                    put("nickname","")
                    put("password",hashedPassword)
                    put("salt",salt)
                    put("sex","")
                    put("address","")
                    put("headPhoto",R.drawable.three)
                    put("signature","")
                    put("regDate","")
                }
                db.insert("user",null,values)

                val values1 = ContentValues().apply {
                    put("userId",username)
                    put("friendId",username)
                    put("agree",1)
                    put("is_interact",0)
                    put("friendType",0)
                }
                db.insert("user_friend",null,values1)


                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(this,"注册成功", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"账号已被注册", Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun hashPassword(password: String, salt: String): String {
        val bytes = (password + salt).toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
    fun generateSalt(length: Int): String {
        val random = SecureRandom()
        val salt = ByteArray(length)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }
    private fun validate(username: String, password: String, confirmPassword: String): Boolean {
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "请重新输入密码", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
